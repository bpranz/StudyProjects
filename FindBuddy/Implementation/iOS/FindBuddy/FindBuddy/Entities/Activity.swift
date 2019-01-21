//
//  Activity.swift
//  FindBuddy
//
//  Created by Markus Schiller on 10.01.18.
//  Copyright © 2018 FindBuddy. All rights reserved.
//

import Foundation
import Firebase
import UIKit
import CoreLocation

protocol DictionaryConvertible {
    var dict: [String: Any] { get }
}

class Activity: DictionaryConvertible {
    
    var id = ""
    var name = ""
    var category = ""
    var startDate = Date()
    var endDate = Date()
    var maxParticipants: Int?
    var info: String?
    var latitude = 0.0
    var longitude = 0.0
    var creator = ""
    
    var dict: [String : Any] {
        return [
            "id": id,
            "name": name,
            "category": category,
            "startDate": [
                "time": (startDate.timeIntervalSince1970 * 1000.0).rounded(),
//                "timezoneOffset": (TimeZone.current.secondsFromGMT(for: startDate)/60)
                "timezoneOffset": 0
            ],
            "endDate": [
                "time": (endDate.timeIntervalSince1970 * 1000.0).rounded(),
//                "timezoneOffset": (TimeZone.current.secondsFromGMT(for: endDate)/60)
                "timezoneOffset": 0
            ],
            "maxParticipants": maxParticipants as Any,
            "info": info as Any,
            "latitude": latitude,
            "longitude": longitude,
            "creator": creator
        ]
    }
    
    init() {
        
    }
    
    init(_ activitySnapshot: DataSnapshot) {
        let snapshotValue = activitySnapshot.value as? [String: Any]
        
        if let id = snapshotValue?["id"] as? String {
            self.id = id
        }
        if let name = snapshotValue?["name"] as? String {
            self.name = name
        }
        if let category = snapshotValue?["category"] as? String {
            self.category = category
        }
        
        if let startDate = createDate(from: snapshotValue?["startDate"] as? [String: Any]) {
            self.startDate = startDate
        }
        
        if let endDate = createDate(from: snapshotValue?["endDate"] as? [String: Any]) {
            self.endDate = endDate
        }
        
        self.maxParticipants = snapshotValue?["maxParticipants"] as? Int
        
        self.info = snapshotValue?["info"] as? String
        
        if let latitude = snapshotValue?["latitude"] as? Double {
            self.latitude = latitude
        }
        
        if let longitude = snapshotValue?["longitude"] as? Double {
            self.longitude = longitude
        }
        
        if let creator = snapshotValue?["creator"] as? String {
            self.creator = creator
        }
    }
    
    private func createDate(from dateSnapshot: [String:Any]?) -> Date? {
        if let time = dateSnapshot?["time"] as? Double {
            if let timezoneOffset = dateSnapshot?["timezoneOffset"] as? Double {
                return Date(timeIntervalSince1970: time/1000 - timezoneOffset*60)
            }
        }
        return Date()
        
        /*var components = DateComponents()
        if let day = dateSnapshot?["date"] as? Int {
            components.day = day
        }
        if let month = dateSnapshot?["month"] as? Int {
            components.month = month + 1
        }
        if let year = dateSnapshot?["year"] as? Int {
            components.year = year + 1900
        }
        if let hour = dateSnapshot?["hours"] as? Int {
            components.hour = hour
        }
        if let minute = dateSnapshot?["minutes"] as? Int {
            components.minute = minute
        }
        return Calendar.current.date(from: components)*/
    }
    
    func getDate() -> String {
        var dateString = ""
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "dd. MMMM yyyy HH:mm"
        dateString = dateFormatter.string(from: startDate)
        if dateFormatter.calendar.startOfDay(for: startDate) == dateFormatter.calendar.startOfDay(for: endDate) {
            dateFormatter.dateFormat = "HH:mm"
        }
        dateString += " - \(dateFormatter.string(from: endDate))"
        return dateString
    }
    
    func getDateStart() -> String {
        var dateString = ""
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "dd.MM.yyyy HH:mm"
        dateString = dateFormatter.string(from: startDate)
        
        return dateString
    }
    
    func getLocation(completion: @escaping (String) -> ()) {
        var locationString = ""
        
        let location = CLLocation(latitude: latitude, longitude: longitude)
        let geocoder = CLGeocoder()
        
        geocoder.reverseGeocodeLocation(location) { (placemarks, error) in
            if(error != nil){
                print("getting Location String failed")
            }
            guard let pm = placemarks else {
                print("no placemarks found")
                return
            }
            
            if pm.count > 0 {
                let pm = placemarks![0]

                if let thoroughfare = pm.thoroughfare {
                    locationString += thoroughfare + " "
                }
                if let subThoroughfare = pm.subThoroughfare {
                    locationString += subThoroughfare + ", "
                }
                if let postalCode = pm.postalCode {
                    locationString += postalCode + " "
                }
                if let locality = pm.locality {
                    locationString += locality
                }
                /*if let country = pm.country {
                    locationString += country + ""
                }*/
                completion(locationString)
            }
        }
    }
}
