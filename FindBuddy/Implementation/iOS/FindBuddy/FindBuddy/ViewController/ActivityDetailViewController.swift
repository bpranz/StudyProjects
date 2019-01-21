//
//  ActivityDetailViewController.swift
//  FindBuddy
//
//  Created by Markus Schiller on 25.01.18.
//  Copyright © 2018 FindBuddy. All rights reserved.
//

import UIKit
import MapKit
import Firebase

class ActivityDetailViewController: UIViewController, MKMapViewDelegate {
    
    var participatedActivitiesRef: DatabaseReference!
    var participantsRef: DatabaseReference!
    
    var activity: Activity?
    
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var categoryLabel: UILabel!
    @IBOutlet weak var dateLabel: UILabel!
    @IBOutlet weak var participantsLabel: UILabel!
    @IBOutlet weak var infoTextView: UITextView!
    @IBOutlet weak var mapView: MKMapView!
    @IBOutlet weak var adressLabel: UILabel!
    
    let teilnehmenString = "Teilnehmen"
    let verlassenString = "Verlassen"
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        participatedActivitiesRef = Database.database().reference().child("ParticipatedActivities")
        participantsRef = Database.database().reference().child("Participants")
        
        // Do any additional setup after loading the view.
        if let ac = activity {
            titleLabel.text = ac.name
            categoryLabel.text = ac.category
            dateLabel.text = ac.getDate()
            if let maxPart = ac.maxParticipants {
                participantsLabel.text = "Maximale Teilnehmer: \(maxPart)"
            }
            else {
                participantsLabel.isHidden = true
            }
            infoTextView.textContainer.lineFragmentPadding = 0
            if let info = ac.info {
                infoTextView.text = info
            }
            else {
                infoTextView.isHidden = true
            }
            
            ac.getLocation(completion: { (location) in
                self.adressLabel.text = location
            })
            
            if let userUid = Auth.auth().currentUser?.uid, userUid == ac.creator {
                self.navigationItem.rightBarButtonItem = UIBarButtonItem(barButtonSystemItem: UIBarButtonSystemItem.trash, target: self, action: #selector(ActivityDetailViewController.rightBarButtonItemTapped(_:)))
            }
            
            participatedActivitiesRef.observeSingleEvent(of: .value, with: { (snapshot) in
                if snapshot.hasChildren() {
                    for participatedActivityUserUid in snapshot.children.allObjects as! [DataSnapshot] {
                        guard let userUid = Auth.auth().currentUser?.uid else {
                            return
                        }
                        if participatedActivityUserUid.key == userUid {
                            for participatedActivity in participatedActivityUserUid.children.allObjects as! [DataSnapshot] {
                                if ac.id == participatedActivity.key {
                                    self.navigationItem.rightBarButtonItem?.title = self.verlassenString
                                }
                            }
                        }
                    }
                }
            })
            
            mapView.delegate = self
            
            let annotation = ActivityPointAnnotation()
            let coordinates = CLLocationCoordinate2D(latitude: ac.latitude,longitude: ac.longitude)
            let span = MKCoordinateSpanMake(0.02,0.02)
            let region = MKCoordinateRegion(center: coordinates, span: span)
            
            var image: UIImage?
            switch ac.category.uppercased() {
            case "SPORT":
                image = #imageLiteral(resourceName: "Sport")
            case "ESSEN":
                image = #imageLiteral(resourceName: "Essen")
            case "UNTERHALTUNG":
                image = #imageLiteral(resourceName: "Unterhaltung")
            case "ENTSPANNUNG":
                image = #imageLiteral(resourceName: "Entspannung")
            case "BILDUNG":
                image = #imageLiteral(resourceName: "Bildung")
            case "TREFFEN":
                image = #imageLiteral(resourceName: "Treffen")
            case "PARTY":
                image = #imageLiteral(resourceName: "Party")
            case "SHOPPING":
                image = #imageLiteral(resourceName: "Shopping")
            default:
                image = #imageLiteral(resourceName: "defaultAnno")
            }
            if let image = image {
                annotation.image = image
            }
            
            mapView.setRegion(region, animated: true)
            annotation.coordinate = CLLocationCoordinate2D(latitude: ac.latitude, longitude: ac.longitude)
            mapView.addAnnotation(annotation)
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func mapView(_ mapView: MKMapView, viewFor annotation: MKAnnotation) -> MKAnnotationView? {
        if !(annotation is ActivityPointAnnotation) {
            return nil
        }
        
        var annotationView = mapView.dequeueReusableAnnotationView(withIdentifier: "pin")
        
        if annotationView == nil {
            annotationView = MKAnnotationView(annotation: annotation, reuseIdentifier: "pin")
            // annotationView?.canShowCallout = false
        }
        else {
            annotationView?.annotation = annotation
        }
        
        let activityAnnotation = annotation as! ActivityPointAnnotation
        annotationView?.image = activityAnnotation.image
        
        return annotationView
    }
    
    @IBAction func rightBarButtonItemTapped(_ sender: UIBarButtonItem) {
        guard let userUid = Auth.auth().currentUser?.uid, let ac = activity else {
            return
        }
        if userUid == ac.creator {
            let activityRef = Database.database().reference().child("activities")
            activityRef.child(ac.id).removeValue()
            
            let createdActivitiesRef = Database.database().reference().child("CreatedActivities")
            createdActivitiesRef.child(userUid).child(ac.id).removeValue()
            
            participantsRef.child(ac.id).removeValue()
            
            participatedActivitiesRef.child(userUid).child(ac.id).removeValue()
            
            navigationController?.popViewController(animated: true)
        }
        else {
            if let title = navigationItem.rightBarButtonItem?.title {
                if title == teilnehmenString {
                    participantsRef.child(ac.id).child(userUid).setValue(userUid)
                    participatedActivitiesRef.child(userUid).child(ac.id).setValue(ac.id)
                    navigationItem.rightBarButtonItem?.title = verlassenString
                } else if title == verlassenString {
                    participantsRef.child(ac.id).child(userUid).removeValue()
                    participatedActivitiesRef.child(userUid).child(ac.id).removeValue()
                    navigationItem.rightBarButtonItem?.title = teilnehmenString
                }
            }
        }
    }
    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
