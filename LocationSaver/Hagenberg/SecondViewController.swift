//
//  SecondViewController.swift
//  Hagenberg
//
//  Created by Bernhard Pranz on 20.12.17.
//  Copyright © 2017 lolly. All rights reserved.
//

import UIKit
import CoreData
import RealmSwift
class SecondViewController: UIViewController, UINavigationControllerDelegate, UIImagePickerControllerDelegate, UITextViewDelegate {
    //Variables
    var imagePickerController : UIImagePickerController!
    var myStringadress = String()
    var myStringlat = String()
    var myStringlong = String()
    var myStringcountry = String()
    var myStringcity = String()
    var myStringstate = String()
    //Storage
    var locationArray:[String] = []
    var myIndex = 0
    var helper = 0
    //Gui Elements
    @IBOutlet weak var adresssecound: UILabel!
    @IBOutlet weak var lat: UILabel!
    @IBOutlet weak var long: UILabel!
    @IBOutlet weak var country: UILabel!
    @IBOutlet weak var city: UILabel!
    @IBOutlet weak var state: UILabel!
    @IBOutlet weak var imageView: UIImageView!
    @IBOutlet weak var locationName: UITextView!

    @IBOutlet weak var infos: UITextView!
    //When Pressed Open Camera
    @IBAction func takephoto(_ sender: Any) {
        imagePickerController = UIImagePickerController()
        imagePickerController.delegate = self
        imagePickerController.sourceType = .camera
        present(imagePickerController, animated: true, completion: nil)
        helper = 1
    }
    //When New space Close Keyboard
    func textView(_ textView: UITextView, shouldChangeTextIn range: NSRange, replacementText text: String) -> Bool {
        if(text == "\n") {
            locationName.resignFirstResponder()
            infos.resignFirstResponder()
            return false
        }
        return true
    }
    //Show Image on Placement
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String : Any]) {
        
        imagePickerController.dismiss(animated: true, completion: nil)
        imageView.image = info[UIImagePickerControllerOriginalImage] as? UIImage
    }
    //Save Image
    func saveImage(imageName: String){
        //create an instance of the FileManager
        let fileManager = FileManager.default
        //get the image path
        let imagePath = (NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true)[0] as NSString).appendingPathComponent(imageName)
        //get the image we took with camera
        let image = imageView.image!
        //get the PNG data for this image
        let data = UIImagePNGRepresentation(image)
        //store it in the document directory
        fileManager.createFile(atPath: imagePath as String, contents: data, attributes: nil)
    }
    //Save Informations
    @IBAction func save(_ sender: Any) {
        if helper == 1 {
            saveImage(imageName: locationName.text)
        }
        

        // Do any additional setup after loading the view, typically from a nib.
        let appDelegate = UIApplication.shared.delegate as! AppDelegate
        let context = appDelegate.persistentContainer.viewContext
        let adress = NSEntityDescription.insertNewObject(forEntityName: "User", into: context)
        adress.setValue(locationName.text, forKey: "locationName")
        adress.setValue(city.text, forKey: "city")
        adress.setValue(country.text, forKey: "country")
        adress.setValue(state.text, forKey: "state")
        adress.setValue(adresssecound.text, forKey: "adress")
        adress.setValue(lat.text, forKey: "lat")
        adress.setValue(long.text, forKey: "long")
        adress.setValue(infos.text, forKey: "infos")
        do {
            try context.save()
            print("save")
        }
        catch{
            
        }
        
    }
    //Import in our Realm Database
    func dataRealm(){
        let user1 = Userdata()
        user1.adress = adresssecound.text!
        user1.name = locationName.text
        user1.city = city.text!
        user1.state = state.text!
        user1.country = country.text!
        user1.lat = lat.text!
        user1.long = long.text!
        
        let realm = try! Realm()
        try! realm.write {
            realm.add(user1)
            print("Added")
        }
    }
    //Query For the Realm Database, prints all all User Locations Names
    func realmquery (){
        let realm = try! Realm()
        let alluser =  realm.objects(Userdata.self)
        for user in alluser {
            print(user.name)
        }
        
        
    }
    override func viewDidLoad() {
        super.viewDidLoad()
        country.text = myStringcountry
        state.text = myStringstate
        city.text = myStringcity
        adresssecound.text = myStringadress
        lat.text = myStringlat
        long.text = myStringlong
        locationName.delegate = self
        infos.delegate = self
        // Do any additional setup after loading the view.
        dataRealm()
        realmquery()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

}
