//
//  ThridViewController.swift
//  Hagenberg
//
//  Created by Bernhard Pranz on 20.12.17.
//  Copyright © 2017 lolly. All rights reserved.
//

import UIKit
import CoreData

class ThridViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    var adressArr:[String] = []
    var locArr:[Locations] = []
    var myIndex = 0
    //Create Rows equal to the Elements we have
    public func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int
    {
        return (adressArr.count)
    }
    //Create Content for Each Cell
    public func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell
    {
        let cell = UITableViewCell(style: UITableViewCellStyle.default, reuseIdentifier: "cell")
        cell.textLabel?.text = adressArr[indexPath.row]
        return cell
    }
    //If Row Selected
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        myIndex = indexPath.row
        performSegue(withIdentifier: "sequee", sender: self)
    }
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        var fourthController =  segue.destination as! fourthViewController
        fourthController.indexx = myIndex
        fourthController.locArr = locArr

    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        let appDelegate = UIApplication.shared.delegate as! AppDelegate
        let context = appDelegate.persistentContainer.viewContext
        
        let request = NSFetchRequest<NSFetchRequestResult>(entityName: "User")
        request.returnsObjectsAsFaults = false
        do {
            let results = try context.fetch(request)
            if results.count > 0{
                for result in results as! [NSManagedObject]
                {
                    if let username = result.value(forKey : "locationName") as? String
                    {
                        if let useradress = result.value(forKey : "adress") as? String
                        {
                            if let usercity = result.value(forKey : "city") as? String
                            {
                                if let usercountry = result.value(forKey : "country") as? String
                                {
                                    if let userlat = result.value(forKey : "lat") as? String
                                    {
                                        if let userlong = result.value(forKey : "long") as? String
                                        {
                                            if let userstate = result.value(forKey : "state") as? String
                                            {
                                                if let userinfos = result.value(forKey : "infos") as? String
                                                {
                                                adressArr.append(username)
                                                let loc = Locations(name: username, infos: userinfos, adress: useradress, lat: userlat, long: userlong, country: usercountry, city: usercity, state: userstate)
                                                locArr.append(loc)
                                              
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
            }
        }
        catch{
            
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
}
