Imports System.Data.Entity
Public Class FlightDialog
    Private bd As flightlogappEntities
    Private user As users
    Private flight As flights

    Public Sub New(user As users)
        InitializeComponent()
        Me.user = user
        newWindow()
        departureSiteComboBox.SelectedItem = bd.landingFields.Find(user.landingField)
        pilotComboBox.SelectedItem = bd.users.Find(user.id)
        monoPilotCheckBox.IsChecked = True
        dayLandingsTextBox.Text = "1"
        nightLandingsTextBox.Text = "0"
    End Sub
    Public Sub New(user As users, flight As flights)
        InitializeComponent()
        Me.user = user
        Me.flight = flight
        newWindow()

        departureSiteComboBox.SelectedItem = bd.landingFields.Find(flight.departureSite)
        departureDateDatePicker.SelectedDate = flight.departureDate.Date
        departureTimeTimePicker.Value = Date.Parse(flight.departureDate.TimeOfDay.ToString)
        arrivalSiteComboBox.SelectedItem = bd.landingFields.Find(flight.arrivalSite)
        arrivalDateDatePicker.SelectedDate = flight.arrivalDate.Date
        arrivalTimeTimePicker.Value = Date.Parse(flight.arrivalDate.TimeOfDay.ToString)
        planeComboBox.SelectedItem = bd.planes.Find(flight.plane)
        pilotComboBox.SelectedItem = bd.users.Find(flight.pilot)
        durationTimePicker.Value = Date.Parse(flight.duration.ToString)
        monoPilotCheckBox.IsChecked = flight.monoPilot
        dayLandingsTextBox.Text = flight.dayLandings
        nightLandingsTextBox.Text = flight.nightLandings
    End Sub
    Sub newWindow()
        bd = New flightlogappEntities
        bd.landingFields.Load()
        bd.planes.Load()
        bd.users.Load()

        departureSiteComboBox.ItemsSource = bd.landingFields.Local
        arrivalSiteComboBox.ItemsSource = bd.landingFields.Local
        planeComboBox.ItemsSource = bd.planes.Local
        pilotComboBox.ItemsSource = bd.users.Local
    End Sub

    Private Sub sendFlightButton_Click(sender As Object, e As RoutedEventArgs)
        Dim duration As TimeSpan
        Dim departureDate, arrivalDate As DateTime
        Dim dayLandings, nightLandings As Integer
        If (flight Is Nothing) Then
            flight = New flights
            flight.departureSite = CType(departureSiteComboBox.SelectedItem, landingFields).id
            If (DateTime.TryParse(departureDateDatePicker.SelectedDate & " " & departureTimeTimePicker.Text, departureDate)) Then
                flight.departureDate = departureDate
            Else
                MessageBox.Show("Please provide a valid departure date", "Invalid departure date", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            flight.arrivalSite = CType(arrivalSiteComboBox.SelectedItem, landingFields).id
            If (DateTime.TryParse(arrivalDateDatePicker.SelectedDate & " " & arrivalTimeTimePicker.Text, arrivalDate)) Then
                flight.arrivalDate = arrivalDate
            Else
                MessageBox.Show("Please provide a valid arrival date", "Invalid arrival date", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            If (planeComboBox.SelectedItem IsNot Nothing) Then
                flight.plane = CType(planeComboBox.SelectedItem, planes).id
            Else
                MessageBox.Show("Please provide a valid plane", "Invalid plane", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            flight.pilot = CType(pilotComboBox.SelectedItem, users).id
            If (TimeSpan.TryParse(durationTimePicker.Text, duration)) Then
                flight.duration = duration
            Else
                MessageBox.Show("Please provide a valid duration format (00:00:00)", "Invalid duration format", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            If (monoPilotCheckBox.IsChecked) Then
                flight.monoPilot = 1
            Else
                flight.monoPilot = 0
            End If
            If (Not dayLandingsTextBox.Text.Equals("") And Integer.TryParse(dayLandingsTextBox.Text, dayLandings)) Then
                flight.dayLandings = dayLandings
            Else
                MessageBox.Show("Please provide a valid day landings number", "Invalid duration format", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            If (Not nightLandingsTextBox.Text.Equals("") And Integer.TryParse(nightLandingsTextBox.Text, nightLandings)) Then
                flight.nightLandings = nightLandings
            Else
                MessageBox.Show("Please provide a valid night landings number", "Invalid duration format", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If

            Dim result = MessageBox.Show("Are you sure you want to add " & bd.landingFields.Find(flight.departureSite).name & " - " & bd.landingFields.Find(flight.arrivalSite).name & " flight?", "Add new flight", MessageBoxButton.YesNo, MessageBoxImage.Question)
            If (result = MessageBoxResult.Yes) Then
                bd.flights.Add(flight)
                bd.SaveChanges()
                MsgBox("Flight succesfully added", MsgBoxStyle.Information)
                Close()
            End If
        ElseIf (flight.id) Then
            flight.departureSite = CType(departureSiteComboBox.SelectedItem, landingFields).id
            If (DateTime.TryParse(departureDateDatePicker.SelectedDate & " " & departureTimeTimePicker.Text, departureDate)) Then
                flight.departureDate = departureDate
            Else
                MessageBox.Show("Please provide a valid departure date", "Invalid departure date", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            flight.arrivalSite = CType(arrivalSiteComboBox.SelectedItem, landingFields).id
            If (DateTime.TryParse(arrivalDateDatePicker.SelectedDate & " " & arrivalTimeTimePicker.Text, arrivalDate)) Then
                flight.arrivalDate = arrivalDate
            Else
                MessageBox.Show("Please provide a valid arrival date", "Invalid arrival date", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            If (planeComboBox.SelectedItem IsNot Nothing) Then
                flight.plane = CType(planeComboBox.SelectedItem, planes).id
            Else
                MessageBox.Show("Please provide a valid plane", "Invalid plane", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            flight.pilot = CType(pilotComboBox.SelectedItem, users).id
            If (TimeSpan.TryParse(durationTimePicker.Text, duration)) Then
                flight.duration = duration
            Else
                MessageBox.Show("Please provide a valid duration format (00:00:00)", "Invalid duration format", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            If (monoPilotCheckBox.IsChecked) Then
                flight.monoPilot = 1
            Else
                flight.monoPilot = 0
            End If
            If (Not dayLandingsTextBox.Text.Equals("") And Integer.TryParse(dayLandingsTextBox.Text, dayLandings)) Then
                flight.dayLandings = dayLandings
            Else
                MessageBox.Show("Please provide a valid day landings number", "Invalid duration format", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            If (Not nightLandingsTextBox.Text.Equals("") And Integer.TryParse(nightLandingsTextBox.Text, nightLandings)) Then
                flight.nightLandings = nightLandings
            Else
                MessageBox.Show("Please provide a valid night landings number", "Invalid duration format", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If

            Dim result = MessageBox.Show("Are you sure you want to update " & bd.landingFields.Find(flight.departureSite).name & " - " & bd.landingFields.Find(flight.arrivalSite).name & " flight?", "Update flight", MessageBoxButton.YesNo, MessageBoxImage.Question)
            If (result = MessageBoxResult.Yes) Then
                bd.SaveChanges()
                MsgBox("Flight succesfully updated", MsgBoxStyle.Information)
                Close()
            End If
        Else
            flight = New flights
            flight.departureSite = CType(departureSiteComboBox.SelectedItem, landingFields).id
            If (DateTime.TryParse(departureDateDatePicker.SelectedDate & " " & departureTimeTimePicker.Text, departureDate)) Then
                flight.departureDate = departureDate
            Else
                MessageBox.Show("Please provide a valid departure date", "Invalid departure date", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            flight.arrivalSite = CType(arrivalSiteComboBox.SelectedItem, landingFields).id
            If (DateTime.TryParse(arrivalDateDatePicker.SelectedDate & " " & arrivalTimeTimePicker.Text, arrivalDate)) Then
                flight.arrivalDate = arrivalDate
            Else
                MessageBox.Show("Please provide a valid arrival date", "Invalid arrival date", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            If (planeComboBox.SelectedItem IsNot Nothing) Then
                flight.plane = CType(planeComboBox.SelectedItem, planes).id
            Else
                MessageBox.Show("Please provide a valid plane", "Invalid plane", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            flight.pilot = CType(pilotComboBox.SelectedItem, users).id
            If (TimeSpan.TryParse(durationTimePicker.Text, duration)) Then
                flight.duration = duration
            Else
                MessageBox.Show("Please provide a valid duration format (00:00:00)", "Invalid duration format", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            If (monoPilotCheckBox.IsChecked) Then
                flight.monoPilot = 1
            Else
                flight.monoPilot = 0
            End If
            If (Not dayLandingsTextBox.Text.Equals("") And Integer.TryParse(dayLandingsTextBox.Text, dayLandings)) Then
                flight.dayLandings = dayLandings
            Else
                MessageBox.Show("Please provide a valid day landings number", "Invalid duration format", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            If (Not nightLandingsTextBox.Text.Equals("") And Integer.TryParse(nightLandingsTextBox.Text, nightLandings)) Then
                flight.nightLandings = nightLandings
            Else
                MessageBox.Show("Please provide a valid night landings number", "Invalid duration format", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If

            Dim result = MessageBox.Show("Are you sure you want to add " & bd.landingFields.Find(flight.departureSite).name & " - " & bd.landingFields.Find(flight.arrivalSite).name & " flight?", "Add new flight", MessageBoxButton.YesNo, MessageBoxImage.Question)
            If (result = MessageBoxResult.Yes) Then
                bd.flights.Add(flight)
                bd.SaveChanges()
                MsgBox("Flight succesfully added", MsgBoxStyle.Information)
                Close()
            End If
        End If
    End Sub
End Class
