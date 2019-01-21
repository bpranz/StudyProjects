//
//  LaunchscreenViewController.swift
//  FindBuddy
//
//  Created by Markus Schiller on 04.02.18.
//  Copyright © 2018 FindBuddy. All rights reserved.
//

import UIKit
import Firebase

class LaunchscreenViewController: UIViewController {

    @IBOutlet weak var iconImageView: UIImageView!
    @IBOutlet weak var typoImageView: UIImageView!
    @IBOutlet weak var backgroundImageView: UIImageView!
    
    var segueIdentifier: String?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        if Auth.auth().currentUser != nil {
            // User is signed in.
            UIView.animate(withDuration: 0.2, delay: 1, options: .curveEaseInOut, animations: {
                    self.iconImageView.transform = CGAffineTransform(scaleX: 0.9, y: 0.9)
            }, completion: { (completed) in
                UIView.animate(withDuration: 0.8, delay: 0, usingSpringWithDamping: 1, initialSpringVelocity: 10, options: .curveEaseInOut, animations: {
                        self.typoImageView.alpha = 0
                        // self.backgroundImageView.alpha = 0.2
                        self.iconImageView.alpha = 0
                        self.iconImageView.transform = CGAffineTransform(scaleX: 2.0, y: 2.0)
                }, completion: { (completed) in
                    self.performSegue(withIdentifier: "launchApp", sender: nil)
                })
            })
        } else {
            // No user is signed in.
            UIView.animate(withDuration: 0.9, delay: 0.2, options: .curveEaseInOut, animations: {
                self.typoImageView.alpha = 0
            }, completion: { (completed) in
                self.performSegue(withIdentifier: "showLoginScreen", sender: nil)
            })
        }
        
        /*guard let segueIdentifier = segueIdentifier
            else {
                let alertController = UIAlertController(title: "Fehler", message: "Etwas ist schief gelaufen. Versuchen Sie es erneut.", preferredStyle: .alert)
                
                let defaultAction = UIAlertAction(title: "OK", style: .cancel, handler: nil)
                alertController.addAction(defaultAction)
                
                self.present(alertController, animated: true, completion: nil)
                return
        }*/
        
        
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    /*func presentedLogin() {
        self.loginController = LoginViewController()
        self.mainController.dismiss(animated: true) {
            UIView.animate(withDuration: 0.2, delay: 0.2, options: .curveEaseInOut, animations: {
                self.typoImageView.alpha = 0
            }, completion: { (completed) in
                //self.performSegue(withIdentifier: "launchscreenTransition", sender: nil)
                self.present(self.loginController, animated: false, completion: nil)
            })
            
        }
    }
    
    func presentMainApplication() {
        self.mainController = TabBarController()
        self.loginController.dismiss(animated: true) {
            self.present(self.mainController, animated: true, completion: nil)
        }
    }*/
    

    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
        switch (segue.identifier ?? "") {
        case "showLoginScreen":
            if let dest = segue.destination as? LoginViewController {
                dest.launchscreenIconFrame = self.iconImageView.frame
            }
        default: break
        }
    }
}
