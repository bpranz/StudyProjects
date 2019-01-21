//
//  TabBarController.swift
//  FindBuddy
//
//  Created by Markus Schiller on 10.01.18.
//  Copyright © 2018 FindBuddy. All rights reserved.
//

import UIKit
import Firebase

class TabBarController: UITabBarController {
    
    var activityRef: DatabaseReference!
    var activities = [Activity]()

    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view.
        activityRef = Database.database().reference().child("activities")
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        activityRef.observe(.value) { (activitiesSnapshot) in
            if activitiesSnapshot.hasChildren() {
                self.activities.removeAll()
                for activity in activitiesSnapshot.children.allObjects as! [DataSnapshot] {
                    let activityToAppend = Activity(activity)
                    self.activities.append(activityToAppend)
                }
                let mapViewNavController = self.viewControllers![0] as! UINavigationController
                let mapViewController = mapViewNavController.topViewController as! MapViewController
                mapViewController.activities = self.activities
                mapViewController.reloadActivities()
                
                let tableViewNavController = self.viewControllers![1] as! UINavigationController
                let activitiesTableViewController = tableViewNavController.topViewController as! ActivitiesTableViewController
                activitiesTableViewController.activities = self.activities
                activitiesTableViewController.reloadActivities()
            }
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
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
