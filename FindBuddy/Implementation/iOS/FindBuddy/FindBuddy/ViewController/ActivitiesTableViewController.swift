//
//  ActivitiesTableViewController.swift
//  FindBuddy
//
//  Created by Markus Schiller on 10.01.18.
//  Copyright © 2018 FindBuddy. All rights reserved.
//

import UIKit
import Firebase

class ActivitiesTableViewController: UITableViewController {
    
    @IBOutlet weak var segmentControl: UISegmentedControl!
    @IBOutlet var activitiesTableView: UITableView!
    var activities = [Activity]()
    var filteredActivities = [Activity]()
    
    var createdActivitiesRef: DatabaseReference!
    var participatedActivitiesRef: DatabaseReference!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        createdActivitiesRef = Database.database().reference().child("CreatedActivities")
        participatedActivitiesRef = Database.database().reference().child("ParticipatedActivities")
        
        // Uncomment the following line to preserve selection between presentations
        // self.clearsSelectionOnViewWillAppear = false

        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem
    }
    
    override func viewWillAppear(_ animated: Bool) {
        segmentControlChanged(segmentControl)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func reloadActivities() {
        if activitiesTableView != nil {
            segmentControlChanged(segmentControl)
        }
    }
    
    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return filteredActivities.count
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "activityCell", for: indexPath) as! ActivityTableViewCell
        let activityForRow = filteredActivities[indexPath.row]
        // Configure the cell...
        cell.titleLabel.text = activityForRow.name
        cell.categoryLabel.text = activityForRow.category
        cell.dateLabel.text = activityForRow.getDateStart()
        // cell.participantsLabel.text = "\(activityForRow.maxParticipants)"
        activityForRow.getLocation { (location) in
            cell.locationLabel.text = location
            cell.locationLabel.isHidden = false
        }

        return cell
    }
    
    /*
    // Override to support conditional editing of the table view.
    override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the specified item to be editable.
        return true
    }
    */

    /*
    // Override to support editing the table view.
    override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            // Delete the row from the data source
            tableView.deleteRows(at: [indexPath], with: .fade)
        } else if editingStyle == .insert {
            // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
        }    
    }
    */

    /*
    // Override to support rearranging the table view.
    override func tableView(_ tableView: UITableView, moveRowAt fromIndexPath: IndexPath, to: IndexPath) {

    }
    */

    /*
    // Override to support conditional rearranging of the table view.
    override func tableView(_ tableView: UITableView, canMoveRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the item to be re-orderable.
        return true
    }
    */

    @IBAction func segmentControlChanged(_ sender: UISegmentedControl) {
        self.filteredActivities.removeAll()
        switch segmentControl.selectedSegmentIndex {
        case 0:
            participatedActivitiesRef.observeSingleEvent(of: .value, with: { (snapshot) in
                if snapshot.hasChildren() {
                    for participatedActivityUserUid in snapshot.children.allObjects as! [DataSnapshot] {
                        guard let userUid = Auth.auth().currentUser?.uid else {
                            return
                        }
                        if participatedActivityUserUid.key == userUid {
                            for participatedActivity in participatedActivityUserUid.children.allObjects as! [DataSnapshot] {
                                for activity in self.activities {
                                    if activity.id == participatedActivity.key {
                                        self.filteredActivities.append(activity)
                                    }
                                }
                            }
                        }
                    }
                }
                self.activitiesTableView.reloadData()
            })
        case 1:
            createdActivitiesRef.observeSingleEvent(of: .value, with: { (snapshot) in
                if snapshot.hasChildren() {
                    for createdActivityUserUid in snapshot.children.allObjects as! [DataSnapshot] {
                        guard let userUid = Auth.auth().currentUser?.uid else {
                            return
                        }
                        if createdActivityUserUid.key == userUid {
                            for createdActivity in createdActivityUserUid.children.allObjects as! [DataSnapshot] {
                                for activity in self.activities {
                                    if activity.id == createdActivity.key {
                                        self.filteredActivities.append(activity)
                                    }
                                }
                            }
                        }
                    }
                }
                self.activitiesTableView.reloadData()
            })
        default:
            fatalError("Unknown segment index \(segmentControl.selectedSegmentIndex)")
        }
        
        
    }
    
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
        super.prepare(for: segue, sender: sender)
        
        switch(segue.identifier ?? "") {
        case "showDetailsOfActivityFromList":
            guard let activityDetailViewController = segue.destination as? ActivityDetailViewController else {
                fatalError("Unexpected destination: \(segue.destination)")
            }
            
            guard let selectedActivityCell = sender as? UITableViewCell else {
                fatalError("Unexpected sender: \(sender ?? "Unknown Sender")")
            }
            
            guard let indexPath = activitiesTableView.indexPath(for: selectedActivityCell) else {
                fatalError("The selected cell is not being displayed by the table")
            }
            
            let selectedActivity = filteredActivities[indexPath.row]
            activityDetailViewController.activity = selectedActivity
        default: fatalError("Unexpected Segue Identifier: \(segue.identifier ?? "Unknown Segue Identifier")")
        }
    }

}
