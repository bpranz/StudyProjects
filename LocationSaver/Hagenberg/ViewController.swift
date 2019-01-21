//
//  ViewController.swift
//  Hagenberg
//
//  Created by lolly on 16.12.17.
//  Copyright © 2017 lolly. All rights reserved.
//

import UIKit
import MapKit
import CoreLocation

class ViewController: UIViewController,CLLocationManagerDelegate {
    
    @IBOutlet weak var map: MKMapView!
    @IBOutlet weak var coordinates: UILabel!
    @IBOutlet weak var latitude: UILabel!
    @IBOutlet weak var longitude: UILabel!
    @IBOutlet weak var adress: UILabel!
    
    var geocoder = CLGeocoder()
    let manager  = CLLocationManager();
    var locality = ""
    var administrativeArea = ""
    var country = ""
    var lat = ""
    var long = ""
    var city = ""
    var state = ""
    var adresss = ""
    var adressnumb = ""
    
   
    @IBAction func next(_ sender: Any) {
        performSegue(withIdentifier: "seque", sender: self)
    }
   
    
    //Funktion welche die aktuellen GPS Daten von User Enthält
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        let userLocation:CLLocation = locations[0] as CLLocation // note that locations is same as the one in the function declaration
        let coordinations = CLLocationCoordinate2D(latitude: userLocation.coordinate.latitude,longitude: userLocation.coordinate.longitude)

        let latitudetext:String = "\(coordinations.latitude)"
        let longitudetext:String = "\(coordinations.longitude)"
        let span = MKCoordinateSpanMake(0.02,0.02)
        let region = MKCoordinateRegion(center: coordinations, span: span)//this basically tells your map where to look and where from what distance
        
        map.setRegion(region, animated: true)
        
    
        geocode(latitude: coordinations.latitude, longitude: coordinations.longitude) { placemark, error in
            guard let placemark = placemark, error == nil else { return }
            // you should always update your UI in the main thread
            DispatchQueue.main.async {
                //  update UI here
                self.lat = latitudetext
                self.long = longitudetext
                self.country = placemark.country!
                self.adresss = placemark.thoroughfare!+" "+placemark.subThoroughfare!
                self.city = placemark.locality!
                self.state = placemark.administrativeArea!
            }
        }

    }
    
    func geocode(latitude: Double, longitude: Double, completion: @escaping (CLPlacemark?, Error?) -> ())  {
        CLGeocoder().reverseGeocodeLocation(CLLocation(latitude: latitude, longitude: longitude)) { placemarks, error in
            guard let placemark = placemarks?.first, error == nil else {
                completion(nil, error)
                return
            }
            completion(placemark, nil)
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        var secondController =  segue.destination as! SecondViewController
        secondController.myStringadress = adresss
        secondController.myStringlat = lat
        secondController.myStringlong = long
        secondController.myStringcity = city
        secondController.myStringcountry = country
        secondController.myStringstate = state
    }

    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        manager.delegate = self
        //Genauigkeit von der Location
        manager.desiredAccuracy = kCLLocationAccuracyBest
        //Wenn der User die App benützt, brauchen wir Gps verbindung
        manager.requestWhenInUseAuthorization();
        //Updates Location
        manager.startUpdatingLocation();
     
        map.showsUserLocation = true
    
        
        
        }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }   }





