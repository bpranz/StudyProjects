//
//  fourthViewController.swift
//  Hagenberg
//
//  Created by a a on 21.12.17.
//  Copyright © 2017 lolly. All rights reserved.
//

import UIKit
import MapKit
import CoreData
class fourthViewController: UIViewController {

    @IBAction func back(_ sender: Any) {
    }
    @IBOutlet weak var name: UILabel!
    
    @IBOutlet weak var infos: UILabel!
    
    @IBOutlet weak var adress: UILabel!
    
    @IBOutlet weak var lat: UILabel!
    
    @IBOutlet weak var long: UILabel!
    
    @IBOutlet weak var country: UILabel!
    
    @IBOutlet weak var city: UILabel!
    
    @IBOutlet weak var state: UILabel!
    
    @IBOutlet weak var imageview: UIImageView!
    
    @IBOutlet weak var mapview: MKMapView!
    
    var locArr:[Locations] = []
    var indexx = 0
    override func viewDidLoad() {
        super.viewDidLoad()
        
        name.text = locArr[indexx].name
        infos.text = locArr[indexx].infos
        adress.text = locArr[indexx].adress
        lat.text = locArr[indexx].lat
        long.text = locArr[indexx].long
        country.text = locArr[indexx].country
        state.text = locArr[indexx].state
        city.text = locArr[indexx].city
        
        let string = NSString(string: lat.text!)
        let stringg = NSString(string: long.text!)
        let annotation = MKPointAnnotation()
        let coordinations = CLLocationCoordinate2D(latitude: string.doubleValue,longitude: stringg.doubleValue)
        let span = MKCoordinateSpanMake(0.02,0.02)
        let region = MKCoordinateRegion(center: coordinations, span: span)//this basically tells your map where to look and where from what distance
        
        mapview.setRegion(region, animated: true)
        annotation.coordinate = CLLocationCoordinate2D(latitude: string.doubleValue, longitude: stringg.doubleValue)
        mapview.addAnnotation(annotation)
        getImage(imageName: name.text!)
        
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func getImage(imageName: String){
        let fileManager = FileManager.default
        let imagePath = (NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true)[0] as NSString).appendingPathComponent(imageName)
        if fileManager.fileExists(atPath: imagePath){
            imageview.image = UIImage(contentsOfFile: imagePath)
        }else{
            print("Panic! No Image!")
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

