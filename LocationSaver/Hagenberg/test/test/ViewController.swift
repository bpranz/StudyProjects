//
//  ViewController.swift
//  test
//
//  Created by a a on 21.12.17.
//  Copyright © 2017 a a. All rights reserved.
//

import UIKit
import CoreData

class ViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
       
        let appDelegate = UIApplication.shared.delegate as! AppDelegate
        let context = appDelegate.persistentContainer.viewContext
        
        let adress = NSEntityDescription.insertNewObject(forEntityName: "User ", into: context)
        adress.setValue("lavendelweg", forKey: "adress")
        
        do {
            try context.save()
            print("save")
        }
        catch{
            print("dindt work")
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }


}

