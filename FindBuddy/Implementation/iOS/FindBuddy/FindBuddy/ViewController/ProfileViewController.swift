//
//  ProfileViewController.swift
//  FindBuddy
//
//  Created by Bernhard Pranz on 04.02.18.
//  Copyright © 2018 FindBuddy. All rights reserved.
//

import UIKit
import Firebase

extension Date {
    var age: Int? {
        return Calendar.current.dateComponents([.year], from: self, to: Date()).year
    }
}

class ProfileViewController: UIViewController, UINavigationControllerDelegate, UIImagePickerControllerDelegate, UITextFieldDelegate {
    
    @IBOutlet weak var profileImageView: UIImageView!
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var activitiesLabel: UILabel!
    @IBOutlet weak var aboutMeTextField: UITextField!
    @IBOutlet weak var profiletextLabel: UILabel!
    
    let imagePicker = UIImagePickerController()
    
    var profilePicturesRef: StorageReference!
    var userRef: DatabaseReference!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        aboutMeTextField.delegate = self
        imagePicker.delegate = self
        
        profileImageView.layer.cornerRadius = profileImageView.frame.size.width / 2
        profileImageView.clipsToBounds = true
        
        profilePicturesRef = Storage.storage().reference().child("ProfilPictures")
        
        if let userUid = Auth.auth().currentUser?.uid {
            userRef = Database.database().reference().child("User").child(userUid)
        }
        
        userRef.observeSingleEvent(of: .value) { (snapshot) in
            if snapshot.hasChildren() {
                let value = snapshot.value as? [String: Any]
                var nameLabelString = ""
                if let name = value?["Name"] as? String {
                    nameLabelString = name
                }
                if let birthdayString = value?["Geburtstag"] as? String {
                    let dateFormatter = DateFormatter()
                    dateFormatter.dateFormat = "dd.MM.yyyy"
                    if let birthday = dateFormatter.date(from: birthdayString), let age = birthday.age {
                        nameLabelString += ", " + String(age)
                    }
                }
                self.nameLabel.text = nameLabelString
                
                if let profiletext = value?["profiltext"] as? String {
                    self.profiletextLabel.text = profiletext
                }
            }
        }
        if let userUid = Auth.auth().currentUser?.uid {
            profilePicturesRef.child(userUid).getData(maxSize: 10 * 2048 * 2048 , completion: { (data, error) in
                if let error = error {
                    print(error.localizedDescription)
                } else {
                    if let data = data, let image = UIImage(data: data) {
                        self.profileImageView.image = image
                    }
                }
            })
        }
        
        // loadData()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }
    
    @IBAction func changePhoto(_ sender: UIButton) {
        imagePicker.sourceType = .photoLibrary
        imagePicker.allowsEditing = false
        self.present(imagePicker, animated: true, completion: nil)
    }
    
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String : Any]) {
        
        if let pickedImage = info[UIImagePickerControllerOriginalImage] as? UIImage {
            
            profileImageView.contentMode = .scaleAspectFill
            profileImageView.image = pickedImage
            
            guard let userUid = Auth.auth().currentUser?.uid else {
                print("No user signed in")
                return
            }
            
            if let uploadData = UIImagePNGRepresentation(pickedImage) {
                profilePicturesRef.child(userUid).putData(uploadData)
            }
        }
            
            /*guard
                let userID = Auth.auth().currentUser?.uid,
                let imageData = UIImageJPEGRepresentation(pickedImage, 1)
            else {
                print("No user logged in or image could not be converted to JPEG")
                return
            }
            let photoRef = Storage.storage().reference().child("ProfilPictures").child(userID)
            /*let task = photoRef.putData(imageData!, metadata: nil){ (metadata, error) in
                print("UPLOAD FINISHED")
                print(metadata ?? "NO METADATA")
                print(error ?? "NO ERROR")
            }*/
            
            // Upload the file to the path "images/rivers.jpg"
            let task = photoRef.putData(imageData, metadata: nil) { (metadata, error) in
                guard let metadata = metadata else {
                    // Uh-oh, an error occurred!
                    return
                }
                // Metadata contains file metadata such as size, content-type, and download URL.
                _ = metadata.downloadURL
            }
            
            task.observe(.progress, handler: { (snapshot) in
                print(snapshot.progress ?? "NO MORE PROGRESS")
            })
            task.resume()
        } else {
            //error
        }*/
        
        self.dismiss(animated: true, completion: nil)
    }
    
    func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
        dismiss(animated: true, completion: nil)
    }
    /*
    func loadData(){
        var ref: DatabaseReference!
        
        ref = Database.database().reference()

        guard let userID = Auth.auth().currentUser?.uid
            else {
                print("No user logged in")
                return
        }
        
        ref.child("User").child(userID).observeSingleEvent(of: .value, with: { (snapshot) in
            // Get user value
            let value = snapshot.value as? NSDictionary
            let name = value?["Name"] as? String ?? ""
            self.nameLabel.text = name
            if let birthday = value?["Geburtstag"] as? String {
            
                let ageComponents = birthday.components(separatedBy: ".")
                
                let dateDOB = Calendar.current.date(from: DateComponents(year:
                    Int(ageComponents[2]), month: Int(ageComponents[1]), day:
                    Int(ageComponents[0])))!

                let myAge = dateDOB.age
                self.nameLabel.text?.append(", ")
                //self.nameLabel.text?.append(String(myAge))
            }
            
            let profiletext = value?["profiltext"] as? String ?? ""
            
            self.profiletextLabel.text = profiletext
        
        }) { (error) in
            print(error.localizedDescription)
        }
        
        let photoRef = Storage.storage().reference().child("ProfilPictures").child(userID)
        
        let task = photoRef.getData(maxSize: 12 * 1024 * 1024) { data, error in
            if let data = data {
                let image = UIImage(data: data)
                self.profileImageView.image = image
            }
            print(error ?? "NO ERROR")
        }
        task.observe(.progress){ (snapshot) in
            print(snapshot.progress ?? "NO MORE PROGRESS")
        }
        
        task.resume()
        
    }*/
    
    @IBAction func changeProfiletext(_ sender: UIButton) {
        guard
            let aboutMeText = aboutMeTextField.text, !aboutMeText.isEmpty else {
                showAlert(title: "Fehler", message: "Über mich darf nicht leer sein")
                return
        }
        
        userRef.child("profiltext").setValue(aboutMeText)
    }
    
    @IBAction func logoutButtonTapped(_ sender: UIBarButtonItem) {
        let firebaseAuth = Auth.auth()
        do {
            try firebaseAuth.signOut()
        } catch _ as NSError {
            let alertController = UIAlertController(title: "Fehler", message: "Es ist ein unerwarteter Fehler aufgetreten. Überprüfen Sie Ihre Internetverbindung", preferredStyle: .alert)
            
            let defaultAction = UIAlertAction(title: "OK", style: .cancel, handler: nil)
            alertController.addAction(defaultAction)
            
            self.present(alertController, animated: true, completion: nil)
        }
        
        //dismiss(animated: false, completion: nil)
        // navigationController?.popToRootViewController(animated: true)
        
        let vc = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "launch")
        present(vc, animated: true, completion: nil)
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


