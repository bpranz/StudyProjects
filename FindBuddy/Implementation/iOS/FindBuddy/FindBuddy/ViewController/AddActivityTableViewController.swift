//
//  AddActivityTableViewController.swift
//  FindBuddy
//
//  Created by Markus Schiller on 01.02.18.
//  Copyright © 2018 FindBuddy. All rights reserved.
//

import UIKit
import MapKit
import Firebase

class AddActivityTableViewController: UITableViewController, UITextFieldDelegate, UITextViewDelegate, CategoryDelegate {
    
    var activity = Activity()
    var coordinate: CLLocationCoordinate2D?
    
    @IBOutlet weak var saveButton: UIBarButtonItem!
    
    @IBOutlet weak var titleTextField: UITextField!
    @IBOutlet weak var categoryLabel: UILabel!
    @IBOutlet weak var maxTextField: UITextField!
    
    @IBOutlet weak var startDateLabel: UILabel!
    @IBOutlet weak var startDatePicker: UIDatePicker!
    var startDatePickerVisible: Bool?
    
    @IBOutlet weak var endDateLabel: UILabel!
    @IBOutlet weak var endDatePicker: UIDatePicker!
    var endDatePickerVisible: Bool?
    
    private var dateLabelInitialTextColor:UIColor!
    private var dateLabelSelectedTextColor = UIColor.orange
    
    var startDateFormatter = DateFormatter()
    var endDateFormatter = DateFormatter()
    
    @IBOutlet weak var infoTextView: UITextView!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        saveButton.isEnabled = false
        
        titleTextField.delegate = self
        titleTextField.becomeFirstResponder()
        
        titleTextField.addTarget(self, action: #selector(editingChanged), for: .editingChanged)
        
        maxTextField.delegate = self
        
        startDateFormatter.dateFormat = "dd.MM.yyyy   HH:mm"
        
        startDatePicker.date = startDateFormatter.calendar.date(bySetting: .minute, value: 0, of: Date())!
        startDateLabel.text = startDateFormatter.string(from: startDatePicker.date)
        
        if let endDate = endDateFormatter.calendar.date(byAdding: .hour, value: 1, to: startDatePicker.date) {
            endDatePicker.date = endDate
        }
        formatEndDate()
        
        infoTextView.delegate = self
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        startDatePickerVisible = false
        startDatePicker.isHidden = true
        
        endDatePickerVisible = false
        endDatePicker.isHidden = true
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        titleTextField.resignFirstResponder()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @objc func editingChanged(_ textField: UITextField) {
        if textField.text?.count == 1 {
            if textField.text?.first == " " || (textField === maxTextField && textField.text?.first == "0") {
                textField.text = ""
                return
            }
        }
        guard
            let title = titleTextField.text, !title.isEmpty
            else {
                saveButton.isEnabled = false
                return
        }
        saveButton.isEnabled = true
    }
    
    func selectedCategory(_ category: String) {
        activity.category = category
        categoryLabel.text = category
        navigationController?.popViewController(animated: true)
    }
    
    func formatEndDate() {
        if (startDateFormatter.calendar.startOfDay(for: startDatePicker.date) == startDateFormatter.calendar.startOfDay(for: endDatePicker.date)) {
            endDateFormatter.dateFormat = "HH:mm"
        }
        else {
            endDateFormatter.dateFormat = "dd.MM.yyyy   HH:mm"
        }
        endDateLabel.text = endDateFormatter.string(from: endDatePicker.date)
        if (endDatePicker.date < startDatePicker.date) {
            let attributeString: NSMutableAttributedString =  NSMutableAttributedString(string: endDateLabel.text!)
            attributeString.addAttribute(NSAttributedStringKey.strikethroughStyle, value: 1, range: NSMakeRange(0, attributeString.length))
            endDateLabel.attributedText = attributeString;
        }
    }
    
    // MARK: - TextField methods
    
    func textFieldDidBeginEditing(_ textField: UITextField) {
        if startDatePickerVisible! {
            hideDatePickerCell(containingDatePicker: startDatePicker)
        }
        if endDatePickerVisible! {
            hideDatePickerCell(containingDatePicker: endDatePicker)
        }
    }
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        self.view.endEditing(true)
        return false
    }
    
    // MARK: - TextView methods
    
    func textViewDidBeginEditing(_ textView: UITextView) {
        if startDatePickerVisible! {
            hideDatePickerCell(containingDatePicker: startDatePicker)
        }
        if endDatePickerVisible! {
            hideDatePickerCell(containingDatePicker: endDatePicker)
        }
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        return 3
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        switch (section) {
        case 0:
            return 3
        case 1:
            return 4
        case 2:
            return 1
        default:
            return 0
        }
    }
    
    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        var height: CGFloat = 44 // Default
        switch indexPath.section {
        case 1:
            switch indexPath.row {
            case 1:
                if let startDatePickerVisible = startDatePickerVisible {
                    height = startDatePickerVisible ? 216 : 0
                }
            case 3:
                if let endDatePickerVisible = endDatePickerVisible {
                    height = endDatePickerVisible ? 216 : 0
                }
            default: break
            }
        case 2: height = 180
        default: break
        }
        
        return height
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        self.view.endEditing(true)
        
        switch indexPath.section {
        case 0:
            if startDatePickerVisible! {
                hideDatePickerCell(containingDatePicker: startDatePicker)
            }
            if endDatePickerVisible! {
                hideDatePickerCell(containingDatePicker: endDatePicker)
            }
        case 1:
            if indexPath.row == 0 {
                if startDatePickerVisible! {
                    hideDatePickerCell(containingDatePicker: startDatePicker)
                }
                else {
                    if endDatePickerVisible! {
                        hideDatePickerCell(containingDatePicker: endDatePicker)
                    }
                    showDatePickerCell(containingDatePicker: startDatePicker)
                }
            }
            
            if indexPath.row == 2 {
                if endDatePickerVisible! {
                    hideDatePickerCell(containingDatePicker: endDatePicker)
                }
                else {
                    if startDatePickerVisible! {
                        hideDatePickerCell(containingDatePicker: startDatePicker)
                    }
                    showDatePickerCell(containingDatePicker: endDatePicker)
                }
            }
        default: break
        }
        tableView.deselectRow(at: indexPath, animated: true)
    }
    
    // Date picker changed
    @IBAction func dateChanged(_ sender: UIDatePicker) {
        if sender === startDatePicker {
            startDateLabel.text = startDateFormatter.string(from: sender.date)
        }
        formatEndDate()
    }
    
    // Animation of date picker
    func showDatePickerCell(containingDatePicker picker: UIDatePicker) {
        if picker === startDatePicker {
            startDatePickerVisible = true
            startDateLabel.textColor = dateLabelSelectedTextColor
        }
        if picker === endDatePicker {
            endDatePickerVisible = true
            endDateLabel.textColor = dateLabelSelectedTextColor
        }
        
        tableView.beginUpdates()
        tableView.endUpdates()
        
        picker.isHidden = false
        picker.alpha = 0.0
        
        UIView.animate(withDuration: 0.25) {
            picker.alpha = 1.0
        }
    }
    
    func hideDatePickerCell(containingDatePicker picker:UIDatePicker) {
        if picker === startDatePicker {
            startDatePickerVisible = false
            startDateLabel.textColor = dateLabelInitialTextColor
        }
        if picker === endDatePicker {
            endDatePickerVisible = false
            endDateLabel.textColor = dateLabelInitialTextColor
        }
        
        tableView.beginUpdates()
        tableView.endUpdates()
        
        UIView.animate(withDuration: 0.25, animations: {
            picker.alpha = 0.0
        }) { (isFinished) in
            picker.isHidden = true
        }
    }
    
    @IBAction func cancelButtonTapped(_ sender: UIBarButtonItem) {
        dismiss(animated: true, completion: nil)
    }
    
    @IBAction func saveButtonTapped(_ sender: UIBarButtonItem) {
        guard
            let title = titleTextField.text, !title.isEmpty,
            let category = categoryLabel.text, category != "Auswählen"
        else {
            let alert = UIAlertController(title: "Fehlerhafte Eingabe", message: "Daten unvollständig\nTitel und Kategorie sind Pflichtfelder", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "OK", style: .default, handler: nil))
            self.present(alert, animated: true, completion: nil)
            return
        }
        activity.name = title
        activity.category = category
        activity.startDate = startDatePicker.date
        activity.endDate = endDatePicker.date
        if let maxParticipants = maxTextField.text  {
            activity.maxParticipants = Int(maxParticipants)
        }
        if !infoTextView.text.isEmpty {
            activity.info = infoTextView.text
        }
        if let latitude = coordinate?.latitude {
            activity.latitude = latitude
        }
        if let longitude = coordinate?.longitude {
            activity.longitude = longitude
        }
        
        guard let userUid = Auth.auth().currentUser?.uid else {
            return
        }

        activity.creator = userUid
        
        let activityRef = Database.database().reference().child("activities")
        activity.id = activityRef.childByAutoId().key
        
        activityRef.child(activity.id).setValue(activity.dict)
        
        let createdActivitiesRef = Database.database().reference().child("CreatedActivities")
        createdActivitiesRef.child(userUid).child(activity.id).setValue(activity.id)
        
        let participantsRef = Database.database().reference().child("Participants")
        participantsRef.child(activity.id).child(userUid).setValue(userUid)
        
        let participatedActivitiesRef = Database.database().reference().child("ParticipatedActivities")
        participatedActivitiesRef.child(userUid).child(activity.id).setValue(activity.id)
        
        dismiss(animated: true, completion: nil)
    }
    
    
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
        
        switch segue.identifier ?? "" {
        case "chooseCategory":
            guard let categoryTableViewController = segue.destination as? CategoryTableViewController else {
                fatalError("Unexpected destination: \(segue.destination)")
            }
            categoryTableViewController.categoryDelegate = self
        default:
            break
        }
    }

}
