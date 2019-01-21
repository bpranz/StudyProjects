//
//  Locations.swift
//  Hagenberg
//
//  Created by a a on 21.12.17.
//  Copyright © 2017 lolly. All rights reserved.
//

import Foundation
import UIKit

class Locations{
    
    var name:String
    var infos:String
    var adress:String
    var lat:String
    var long:String
    var country:String
    var city:String
    var state:String

    init(name:String, infos:String, adress:String, lat:String, long:String, country:String, city:String, state:String){
        self.name = name
        self.infos = infos
        self.adress = adress
        self.lat = lat
        self.long = long
        self.country = country
        self.city = city
        self.state = state
    }
    
    deinit{
        
    }
    
    
}
