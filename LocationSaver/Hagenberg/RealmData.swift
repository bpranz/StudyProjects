//
//  RealmData.swift
//  LocationSaver
//
//  Created by a a on 22.12.17.
//  Copyright © 2017 lolly. All rights reserved.
//

import Foundation
import RealmSwift
//Class with Variables for our Realm Database
class Userdata : Object {
    @objc dynamic var name = ""
    @objc dynamic var adress = ""
    @objc dynamic var city = ""
    @objc dynamic var state = ""
    @objc dynamic var country = ""
    @objc dynamic var lat = ""
    @objc dynamic var long = ""
}
