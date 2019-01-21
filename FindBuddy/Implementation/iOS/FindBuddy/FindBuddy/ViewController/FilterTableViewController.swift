//
//  FilterTableViewController.swift
//  FindBuddy
//
//  Created by Markus Schiller on 06.02.18.
//  Copyright © 2018 FindBuddy. All rights reserved.
//

import UIKit

protocol FilterDelegate: class {
    func selectedCategories(_ categories: [String])
}

class FilterTableViewController: UITableViewController {
    
    weak var filterDelegate: FilterDelegate?
    var categories: [String]?
    
    @IBOutlet var categoryTableView: UITableView!
    @IBOutlet weak var doneButton: UIBarButtonItem!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        

        // Uncomment the following line to preserve selection between presentations
        // self.clearsSelectionOnViewWillAppear = false

        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        for cell in tableView.visibleCells {
            if let categories = self.categories, let category = cell.textLabel?.text {
                if categories.contains(category) {
                    cell.accessoryType = .checkmark
                }
            }
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 8
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        guard var categories = self.categories else {
            return
        }
        if let selectedCell = tableView.cellForRow(at: indexPath) {
            if selectedCell.accessoryType == .none {
                selectedCell.accessoryType = .checkmark
                if let category = selectedCell.textLabel?.text {
                    categories.append(category)
                }
            }
            else if selectedCell.accessoryType == .checkmark {
                selectedCell.accessoryType = .none
                if let category = selectedCell.textLabel?.text, let index = categories.index(of: category) {
                    categories.remove(at: index)
                }
            }
            selectedCell.isSelected = false
        }
        
        self.categories = categories
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

    @IBAction func doneButtonTapped(_ sender: UIBarButtonItem) {
        if let categories = self.categories {
            filterDelegate?.selectedCategories(categories)
        }
        dismiss(animated: true, completion: nil)
    }
    
    @IBAction func cancelButtonTapped(_ sender: UIBarButtonItem) {
        dismiss(animated: true, completion: nil)
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
