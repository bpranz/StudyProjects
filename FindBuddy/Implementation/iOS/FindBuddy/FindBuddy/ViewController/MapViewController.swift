//
//  FirstViewController.swift
//  FindBuddy
//
//  Created by Markus Schiller on 10.01.18.
//  Copyright © 2018 FindBuddy. All rights reserved.
//

import UIKit
import MapKit

class MapViewController: UIViewController, CLLocationManagerDelegate, MKMapViewDelegate, UIGestureRecognizerDelegate, FilterDelegate {
    
    @IBOutlet weak var mapView: MKMapView!
    @IBOutlet weak var currentLocationButton: UIButton!
    
    var activities = [Activity]()
    var filteredActivities = [Activity]()
    let locManager = CLLocationManager()
    var currentLocation: CLLocation?
    
    var categories = [String]()
    
    var addMode = false
    var gestureRecognizer: UIGestureRecognizer?
    var selectedLocationAnnotation: ActivityPointAnnotation?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        addNavBarImage()
        mapView.delegate = self
        locManager.delegate = self
        locManager.desiredAccuracy = kCLLocationAccuracyHundredMeters
        
        zoomToCurrentLocation()
        
        var image = #imageLiteral(resourceName: "filter")
        image = image.withRenderingMode(.alwaysOriginal)
        self.navigationItem.leftBarButtonItem = UIBarButtonItem(image: image, style:.plain, target: self, action: #selector(MapViewController.filterButtonTapped(_:)))
        
        currentLocationButton.layer.cornerRadius = 5
    }
    
    
    
    override func viewWillAppear(_ animated: Bool) {
        exitAddMode()
        
        locManager.requestWhenInUseAuthorization()
        
        if (CLLocationManager.authorizationStatus() == CLAuthorizationStatus.authorizedWhenInUse ||
            CLLocationManager.authorizationStatus() == CLAuthorizationStatus.authorizedAlways){
            currentLocation = locManager.location
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func addNavBarImage() {
        guard let navController = navigationController else {
            return
        }
        
        let image = #imageLiteral(resourceName: "TypoWhitespace")
        let imageView = UIImageView(image: image)
        
        let bannerWidth = navController.navigationBar.frame.size.width
        let bannerHeight = navController.navigationBar.frame.size.height
        
        let bannerX = bannerWidth / 2 - image.size.width / 2
        let bannerY = bannerHeight / 2 - image.size.height / 2
        
        imageView.frame = CGRect(x: bannerX, y: bannerY, width: bannerWidth, height: bannerHeight)
        imageView.contentMode = .scaleAspectFit
        imageView.center = navController.navigationBar.center
        
        navigationItem.titleView = imageView
    }
    
    func selectedCategories(_ categories: [String]) {
        self.categories = categories
        self.filteredActivities.removeAll()
        if categories.isEmpty {
            filteredActivities = activities
        }
        else {
            for activity in activities {
                if categories.contains(activity.category) {
                    filteredActivities.append(activity)
                }
            }
        }
        reloadAnnotations()
    }
    
    func zoomToCurrentLocation() {
        if let currentLocation = self.currentLocation {
            let mapSpan: MKCoordinateSpan = MKCoordinateSpanMake(0.2, 0.2)
            let userLocation: CLLocationCoordinate2D = CLLocationCoordinate2DMake(currentLocation.coordinate.latitude, currentLocation.coordinate.longitude)
            let region: MKCoordinateRegion = MKCoordinateRegionMake(userLocation, mapSpan)
            self.mapView.setRegion(region, animated: true)
        }
    }
    
    func reloadActivities() {
        selectedCategories(self.categories)
    }
    
    func reloadAnnotations() {
        removeAnnotations()
        for activity in filteredActivities {
            let anno = ActivityPointAnnotation()
            anno.coordinate = CLLocationCoordinate2D(latitude: activity.latitude, longitude: activity.longitude)
            anno.title = activity.name
            
            anno.subtitle = activity.getDate()
            
            var image: UIImage?
            switch activity.category.uppercased() {
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
                anno.image = image
            }
            
            anno.activity = activity
            
            self.mapView.addAnnotation(anno)
        }
    }
    
    func removeAnnotations(){
        for anno in mapView.annotations {
            mapView.removeAnnotation(anno)
        }
    }
    
    func mapView(_ mapView: MKMapView, viewFor annotation: MKAnnotation) -> MKAnnotationView? {
        if !(annotation is ActivityPointAnnotation) {
            return nil
        }
        
        var annotationView = mapView.dequeueReusableAnnotationView(withIdentifier: "pin")
        
        if annotationView == nil {
            annotationView = MKAnnotationView(annotation: annotation, reuseIdentifier: "pin")
            annotationView?.canShowCallout = true
            
            let rightButton = UIButton(type: .detailDisclosure)
            annotationView?.rightCalloutAccessoryView = rightButton as UIView
        }
        else {
            annotationView?.annotation = annotation
        }
        
        let activityAnnotation = annotation as! ActivityPointAnnotation
        annotationView?.image = activityAnnotation.image
        
        return annotationView
    }
    
    func mapView(_ mapView: MKMapView, annotationView view: MKAnnotationView, calloutAccessoryControlTapped control: UIControl) {
        performSegue(withIdentifier: "showDetailsOfActivityFromMap", sender: view)
    }
    
    @objc func handleTap(sender: UITapGestureRecognizer) {
        if sender.state == .ended {
            if addMode {
                if let selLocAnno = selectedLocationAnnotation {
                    mapView.removeAnnotation(selLocAnno)
                }
                let location = sender.location(in: mapView)
                let coordinate = mapView.convert(location, toCoordinateFrom: mapView)
                
                // Add annotation
                selectedLocationAnnotation = ActivityPointAnnotation()
                selectedLocationAnnotation?.coordinate = coordinate
                selectedLocationAnnotation?.image = #imageLiteral(resourceName: "defaultAnno")
                mapView.addAnnotation(selectedLocationAnnotation!)
                
                self.navigationItem.rightBarButtonItem?.isEnabled = true
            }
        }
    }
    
    func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        if (CLLocationManager.authorizationStatus() == CLAuthorizationStatus.authorizedWhenInUse ||
            CLLocationManager.authorizationStatus() == CLAuthorizationStatus.authorizedAlways){
            currentLocation = locManager.location
        }
        self.viewDidLoad()
    }
    
    // MARK: - Button Actions
    
    @IBAction func currentLocationButtonTapped(_ sender: UIButton) {
        zoomToCurrentLocation()
    }
    @IBAction func addButtonTapped(_ sender: UIBarButtonItem) {
        if addMode, let _ = selectedLocationAnnotation {
            performSegue(withIdentifier: "addActivity", sender: nil)
        }
        else {
            showToast(message: "Standort auswählen")
            gestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(MapViewController.handleTap(sender:)))
            gestureRecognizer?.delegate = self
            mapView.addGestureRecognizer(gestureRecognizer!)
            self.navigationItem.leftBarButtonItem = UIBarButtonItem(barButtonSystemItem: UIBarButtonSystemItem.stop, target: self, action: #selector(MapViewController.cancelButtonTapped(_:)))
            self.navigationItem.rightBarButtonItem = UIBarButtonItem(barButtonSystemItem: UIBarButtonSystemItem.done, target: self, action: #selector(MapViewController.addButtonTapped(_:)))
            self.navigationItem.rightBarButtonItem?.isEnabled = false
            addMode = true
        }
    }
    @IBAction func cancelButtonTapped(_ sender: UIBarButtonItem) {
        exitAddMode()
    }
    
    @IBAction func filterButtonTapped(_ sender: UIBarButtonItem) {
        performSegue(withIdentifier: "showFilter", sender: nil)
    }
    
    func exitAddMode() {
        if addMode {
            self.addMode = false
            var image = #imageLiteral(resourceName: "filter")
            image = image.withRenderingMode(.alwaysOriginal)
            self.navigationItem.leftBarButtonItem = UIBarButtonItem(image: image, style:.plain, target: self, action: #selector(MapViewController.filterButtonTapped(_:)))
            self.navigationItem.rightBarButtonItem = UIBarButtonItem(barButtonSystemItem: UIBarButtonSystemItem.add, target: self, action: #selector(MapViewController.addButtonTapped(_:)))
            if let selLocAnno = self.selectedLocationAnnotation {
                self.mapView.removeAnnotation(selLocAnno)
            }
            self.selectedLocationAnnotation = nil
            if let gestureRecognizer = self.gestureRecognizer {
                self.mapView.removeGestureRecognizer(gestureRecognizer)
            }
        }
    }
    
    // MARK: - Navigation
    
    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
        super.prepare(for: segue, sender: sender)
        
        switch segue.identifier ?? "" {
        case "showDetailsOfActivityFromMap":
            guard let activityDetailViewController = segue.destination as? ActivityDetailViewController else {
                fatalError("Unexpected destination: \(segue.destination)")
            }
            if sender is MKAnnotationView {
                guard let annotationView = sender as? MKAnnotationView else {
                    fatalError("Unexpected sender: \(sender ?? "Unknown Sender")")
                }
                let annotation = annotationView.annotation as! ActivityPointAnnotation
                activityDetailViewController.activity = annotation.activity
            }
        case "addActivity":
            guard let addActivityTableViewController = (segue.destination as? UINavigationController)?.childViewControllers.first as? AddActivityTableViewController else {
                fatalError("Unexpected destination: \(segue.destination)")
            }
            if let selLocAnno = selectedLocationAnnotation {
                addActivityTableViewController.coordinate = selLocAnno.coordinate
            }
        case "showFilter":
            guard let filterTableViewController = (segue.destination as? UINavigationController)?.childViewControllers.first as? FilterTableViewController else {
                fatalError("Unexpected destination: \(segue.destination)")
            }
            filterTableViewController.categories = self.categories
            filterTableViewController.filterDelegate = self
        default: break
        }
    }
}
extension UIViewController {
    
    func showToast(message : String) {
        let toastLabel = UILabel(frame: CGRect(x: self.view.frame.size.width/2 - 75, y: self.view.frame.size.height-100, width: 175, height: 35))
        toastLabel.backgroundColor = UIColor.black.withAlphaComponent(0.6)
        toastLabel.textColor = UIColor.white
        toastLabel.textAlignment = .center;
        //toastLabel.font = UIFont(name: "Montserrat-Light", size: 12.0)
        toastLabel.text = message
        toastLabel.alpha = 0.0
        toastLabel.layer.cornerRadius = 10;
        toastLabel.clipsToBounds  =  true
        self.view.addSubview(toastLabel)
        UIView.animate(withDuration: 0.2, delay: 0.0, options: .curveEaseIn, animations: {
            toastLabel.alpha = 1.0
        }) { (isCompleted) in
            UIView.animate(withDuration: 2.0, delay: 2.0, options: .curveEaseIn, animations: {
                toastLabel.alpha = 0.0
            }, completion: {(isCompleted) in
                toastLabel.removeFromSuperview()
            })
        }
        
    }
}

