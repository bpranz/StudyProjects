//
//  NewAccountViewController.swift
//  FindBuddy
//
//  Created by Markus Schiller on 04.02.18.
//  Copyright © 2018 FindBuddy. All rights reserved.
//

import UIKit
import Firebase

class NewAccountViewController: UIViewController, UITextFieldDelegate {

    @IBOutlet weak var nameTextField: UITextField!
    @IBOutlet weak var birthdayTextField: UITextField!
    @IBOutlet weak var emailTextField: UITextField!
    @IBOutlet weak var passwordTextField: UITextField!
    @IBOutlet weak var verifyPasswordTextField: UITextField!
    
    @IBOutlet weak var cancelButton: UIButton!
    @IBOutlet weak var createAccountButton: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        nameTextField.delegate = self
        birthdayTextField.delegate = self
        emailTextField.delegate = self
        passwordTextField.delegate = self
        verifyPasswordTextField.delegate = self
        
        nameTextField.becomeFirstResponder()
        
        self.hideKeyboardWhenTappedAround()
        
        cancelButton.layer.cornerRadius = 5
        createAccountButton.layer.cornerRadius = 5
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        if textField === nameTextField {
            birthdayTextField.becomeFirstResponder()
        }
        if textField === birthdayTextField {
            emailTextField.becomeFirstResponder()
        }
        if textField === emailTextField {
            passwordTextField.becomeFirstResponder()
        }
        if textField === passwordTextField {
            verifyPasswordTextField.becomeFirstResponder()
        }
        if textField === verifyPasswordTextField {
            verifyPasswordTextField.resignFirstResponder()
        }
        return false
    }
    
    @IBAction func cancelButtonTapped(_ sender: UIButton) {
        dismiss(animated: true, completion: nil)
    }
    
    @IBAction func createAccountButtonTapped(_ sender: UIButton) {
        // Name check
        guard
            let name = nameTextField.text, !name.isEmpty
            else {
                showAlert(title: "Name darf nicht leer sein", message: "Es wurde kein Name eingegeben.")
                return
        }
        
        // Birthday check
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "dd.MM.yyyy"
        guard
            let birthday = birthdayTextField.text, !birthday.isEmpty, (dateFormatter.date(from: birthday) != nil)
            else {
                showAlert(title: "Geburtsdatum ungültig", message: "Das eingegebene Geburtsdatum ist nicht im richtigen Format (dd.MM.yyyy)")
                return
        }
        
        // E-Mail check
        guard
            let email = emailTextField.text, !email.isEmpty, email.contains("@"), !email.contains(" ")
            else {
                showAlert(title: "E-Mail ungültig", message: "Die eingegebe E-Mail-Adresse ist nicht gültig.")
                return
        }
        
        // Password check
        guard
            let password = passwordTextField.text, !password.isEmpty, !password.contains(" "), password.count >= 6,
            let verifyPassword = verifyPasswordTextField.text, !verifyPassword.isEmpty, !verifyPassword.contains(" "),
                verifyPassword.count >= 6
            else {
                showAlert(title: "Passwort ungültig", message: "Das Password darf keine Leerzeichen enthalten und muss mindestens 6 Zeichen lang sein.")
                return
        }
        guard password == verifyPassword else {
            showAlert(title: "Passwörter stimmen nicht überein", message: "")
            return
        }
        
        Auth.auth().createUser(withEmail: email, password: password) { (user, error) in
            if error != nil {
                let userRef = Database.database().reference().child("User")
                let userDict = [
                    "Name": name,
                    "Geburtstag": birthday,
                    "Email": email
                ]
                
                if let userUid = Auth.auth().currentUser?.uid {
                    userRef.child(userUid).setValue(userDict)
                }
                
                let alertController = UIAlertController(title: "Neuer Account erstellt", message: "Account erstellen erfolgreich. Sie werden mit dem neuen Account angemeldet.", preferredStyle: .alert)
                
                let defaultAction = UIAlertAction(title: "OK", style: .cancel, handler: { (action) in
                    self.performSegue(withIdentifier: "launchAppAfterNewAccount", sender: nil)
                })
                alertController.addAction(defaultAction)
                
                self.present(alertController, animated: true, completion: nil)
                
            }
            else {
                self.showAlert(title: "Account erstellen fehlgeschlagen", message: "Ein Fehler ist aufgetreten. Bitte überprüfen Sie Ihre Internetverbindung")
            }
        }
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
