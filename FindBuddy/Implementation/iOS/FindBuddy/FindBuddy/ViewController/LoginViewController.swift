//
//  LoginViewController.swift
//  FindBuddy
//
//  Created by Markus Schiller on 04.02.18.
//  Copyright © 2018 FindBuddy. All rights reserved.
//

import UIKit
import Firebase

class LoginViewController: UIViewController, UITextFieldDelegate {

    @IBOutlet weak var iconImageView: UIImageView!
    
    @IBOutlet weak var stackView: UIStackView!
    @IBOutlet weak var emailTextField: UITextField!
    @IBOutlet weak var passwordTextField: UITextField!
    @IBOutlet weak var loginButton: UIButton!
    @IBOutlet weak var newAccountButton: UIButton!
    
    var iconFrame = CGRect()
    var launchscreenIconFrame: CGRect?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        emailTextField.delegate = self
        passwordTextField.delegate = self
        
        loginButton.layer.cornerRadius = 5
        
        //NotificationCenter.default.addObserver(self, selector: #selector(LoginViewController.keyboardWillShow), name: NSNotification.Name.UIKeyboardWillShow, object: nil)
        //NotificationCenter.default.addObserver(self, selector: #selector(LoginViewController.keyboardWillHide), name: NSNotification.Name.UIKeyboardWillHide, object: nil)
        
        self.hideKeyboardWhenTappedAround()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        iconFrame = self.iconImageView.frame
        stackView.alpha = 0.0
        stackView.frame = stackView.frame.offsetBy(dx: 0, dy: 100)
        newAccountButton.alpha = 0.0
        
        if let frame = self.launchscreenIconFrame {
            self.iconImageView.frame = frame
            UIView.animate(withDuration: 0.7, delay: 0, options: .curveEaseInOut, animations: {
                self.iconImageView.frame = self.iconFrame
                self.stackView.alpha = 1.0
                self.stackView.frame = self.stackView.frame.offsetBy(dx: 0, dy: -100)
                self.newAccountButton.alpha = 1.0
            }, completion: nil)
        }
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name.UIKeyboardWillShow, object: nil)
        NotificationCenter.default.removeObserver(self, name: NSNotification.Name.UIKeyboardWillHide, object: nil)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @objc func keyboardWillShow(notification: NSNotification) {
        if let keyboardSize = (notification.userInfo?[UIKeyboardFrameBeginUserInfoKey] as? NSValue)?.cgRectValue {
            if self.view.frame.origin.y == 0{
                /*
                self.stackView.frame.origin.y -= keyboardSize.height * 0.45
                self.newAccountButton.frame.origin.y -= keyboardSize.height * 0.95
                */
                self.view.frame.origin.y -= keyboardSize.height * 0.4
            }
        }
    }
    
    @objc func keyboardWillHide(notification: NSNotification) {
        if let keyboardSize = (notification.userInfo?[UIKeyboardFrameBeginUserInfoKey] as? NSValue)?.cgRectValue {
            if self.view.frame.origin.y != 0{
                /*self.stackView.frame.origin.y += keyboardSize.height * 0.45
                self.newAccountButton.frame.origin.y += keyboardSize.height * 0.95*/
                self.view.frame.origin.y += keyboardSize.height * 0.4
            }
        }
    }
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        if textField === emailTextField {
            passwordTextField.becomeFirstResponder()
        }
        if textField === passwordTextField {
            passwordTextField.resignFirstResponder()
            loginButtonTapped(loginButton)
        }
        return false
    }
    
    @IBAction func loginButtonTapped(_ sender: UIButton) {
        guard
            let email = self.emailTextField.text, !email.isEmpty, email.contains("@"), !email.contains(" "),
            let password = self.passwordTextField.text, !password.isEmpty, !password.contains(" ")
        else {
            showAlert(title: "E-Mail oder Passwort ungültig", message: "Die eingebene E-Mail-Adresse und/oder das eingebene Passwort sind ungültig")
            return
        }
        Auth.auth().signIn(withEmail: email, password: password) { (user, error) in
            if error == nil {
                UIView.animate(withDuration: 0.2, delay: 0, options: .curveEaseInOut, animations: {
                    self.iconImageView.transform = CGAffineTransform(scaleX: 0.9, y: 0.9)
                }, completion: { (completed) in
                    UIView.animate(withDuration: 1, delay: 0, usingSpringWithDamping: 1, initialSpringVelocity: 6.5, options: .curveEaseInOut, animations: {
                        //self.stackView.frame = self.stackView.frame.offsetBy(dx: 0, dy: UIScreen.main.bounds.height)
                        self.stackView.transform = CGAffineTransform(translationX: 0, y: UIScreen.main.bounds.height)
                        self.stackView.alpha = 0
                        // self.backgroundImageView.alpha = 0.2
                        self.iconImageView.alpha = 0
                        self.iconImageView.transform = CGAffineTransform(scaleX: 4, y: 4)
                        self.iconImageView.center = CGPoint(x: UIScreen.main.bounds.width/2, y: UIScreen.main.bounds.height/2)
                        self.newAccountButton.alpha = 0
                    }, completion: { (completed) in
                        self.performSegue(withIdentifier: "launchAppAfterLogin", sender: nil)
                    })
                })
            } else {
                //Tells the user that there is an error and then gets firebase to tell them the error
                self.showAlert(title: "E-Mail oder Passwort ungültig", message: "Die eingebene E-Mail-Adresse und/oder das eingebene Passwort sind ungültig oder es besteht keine Internetverbindung")
            }
        }
    }
    @IBAction func forgotPasswordButtonTapped(_ sender: UIButton) {
        let alertController = UIAlertController(title: "Passwort zurücksetzen", message: "Bitte geben Sie Ihre E-Mail-Adresse ein", preferredStyle: .alert)
        
        let action = UIAlertAction(title: "Zurücksetzen", style: .default) { (alertAction) in
            guard
                let email = (alertController.textFields![0] as UITextField).text, !email.isEmpty, email.contains("@"), !email.contains(" ")
            else {
                self.showAlert(title: "E-Mail-Adresse ungültig", message: "Die eingegebe E-Mail-Adresse ist ungültig")
                return
            }
            Auth.auth().sendPasswordReset(withEmail: email) { (error) in
                if error != nil {
                    self.showAlert(title: "Keine E-Mail-Adresse gefunden", message: "Zu der eingegebnen E-Mail-Adresse wurde kein Account gefunden oder es besteht keine Internetverbindung")
                }
                else {
                    self.showAlert(title: "\"Passwort zurücksetzen\"-Mail versendet", message: "Es wurde eine E-Mail zum Zurücksetzten des Passwortes an die eingegebene E-Mail-Adresse gesendet")
                }
            }
        }
        
        alertController.addTextField { (textField) in
            textField.placeholder = "E-Mail"
            textField.keyboardType = .emailAddress
        }
        
        alertController.addAction(action)
        
        let cancelAction = UIAlertAction(title: "Abbrechen", style: .cancel, handler: nil)
        alertController.addAction(cancelAction)
        
        self.present(alertController, animated: true, completion: nil)
    }
    
    @IBAction func newAccountButtonTapped(_ sender: UIButton) {
        performSegue(withIdentifier: "createNewAccount", sender: nil)
    }
}
extension UIViewController {
    func showAlert(title: String, message: String) {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: .alert)
        
        let defaultAction = UIAlertAction(title: "OK", style: .cancel, handler: nil)
        alertController.addAction(defaultAction)
        
        self.present(alertController, animated: true, completion: nil)
    }
    
    func hideKeyboardWhenTappedAround() {
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(UIViewController.dismissKeyboard))
        tap.cancelsTouchesInView = false
        view.addGestureRecognizer(tap)
    }
    
    @objc func dismissKeyboard() {
        view.endEditing(true)
    }
}

