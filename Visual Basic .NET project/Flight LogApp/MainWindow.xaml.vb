Imports System.Data.Entity
Class MainWindow
    Inherits Window
    Private bd As flightlogappEntities
    Private viewFlights, viewBookings, viewPlanes, viewLandingFields, viewUserPlanes, viewUsers As CollectionViewSource
    Private user As users

    Public Sub New()
        InitializeComponent()
        bd = New flightlogappEntities
        viewFlights = New CollectionViewSource
        viewBookings = New CollectionViewSource
        viewPlanes = New CollectionViewSource
        viewLandingFields = New CollectionViewSource
        viewUsers = New CollectionViewSource
        viewUserPlanes = New CollectionViewSource
        bd.users.Load()
        user = bd.users.Local.Item(4)
        newWindow()
    End Sub
    Public Sub New(user As users)
        InitializeComponent()
        bd = New flightlogappEntities
        viewFlights = New CollectionViewSource
        viewBookings = New CollectionViewSource
        viewPlanes = New CollectionViewSource
        viewLandingFields = New CollectionViewSource
        viewUsers = New CollectionViewSource
        viewUserPlanes = New CollectionViewSource
        Me.user = user
        newWindow()
    End Sub
    Sub newWindow()
        If (user.UserTypes.type.Equals("Controller")) Then
            planesDataGrid.IsEnabled = True
            addPlaneButton.IsEnabled = True
            updatePlaneButton.IsEnabled = True
            deletePlaneButton.IsEnabled = True
            landingFieldsDataGrid.IsEnabled = True
            addLandingFieldButton.IsEnabled = True
            updateLandingFieldButton.IsEnabled = True
            deleteLandingFieldButton.IsEnabled = True
            usersTabItem.IsEnabled = True
        End If
    End Sub

    Private Sub TabControl_SelectionChanged(sender As Object, e As SelectionChangedEventArgs)
        Dim tabControl = CType(sender, TabControl)
        Dim tabItem = CType(tabControl.SelectedItem, TabItem)

        If (tabItem.Header.ToString.Equals("Flights")) Then
            bd.flights.Load()
            viewFlights.Source = bd.flights.Local
            flightsDataGrid.DataContext = viewFlights
            AddHandler viewFlights.Filter, AddressOf viewFlights_Filter
            viewFlights.View.Refresh()
        ElseIf (tabItem.Header.ToString.Equals("Bookings")) Then
            bd.bookings.Load()
            viewBookings.Source = bd.bookings.Local
            bookingsDataGrid.DataContext = viewBookings
            AddHandler viewBookings.Filter, AddressOf viewBookings_Filter
            viewBookings.View.Refresh()
        ElseIf (tabItem.Header.ToString.Equals("Planes")) Then
            bd.planes.Load()
            viewPlanes.Source = bd.planes.Local
            planesDataGrid.DataContext = viewPlanes
            AddHandler viewPlanes.Filter, AddressOf viewPlanes_Filter
            viewPlanes.View.Refresh()
        ElseIf (tabItem.Header.ToString.Equals("Landing Fields")) Then
            bd.landingFields.Load()
            viewLandingFields.Source = bd.landingFields.Local
            landingFieldsDataGrid.DataContext = viewLandingFields
            AddHandler viewLandingFields.Filter, AddressOf viewLandingFields_Filter
        ElseIf (tabItem.Header.ToString.Equals("Users")) Then
            bd.users.Load()
            viewUsers.Source = bd.users.Local
            usersDataGrid.DataContext = viewUsers
            AddHandler viewUsers.Filter, AddressOf viewUsers_Filter
        ElseIf (tabItem.Header.ToString.Equals("Profile")) Then
            bd.landingFields.Load()
            nameTextBox.Text = user.name
            lastnameTextBox.Text = user.lastname
            userTypeTextBox.Text = user.UserTypes.type
            landingFieldComboBox.ItemsSource = bd.landingFields.Local
            landingFieldComboBox.SelectedItem = bd.landingFields.Find(user.landingField)
            usernameTextBox.Text = user.username
            passwordPasswordBox.Password = user.password

            bd.planes.Load()
            viewUserPlanes.Source = bd.planes.Local
            userPlanesDataGrid.DataContext = viewUserPlanes
            AddHandler viewUserPlanes.Filter, AddressOf viewUserPlanes_Filter
            viewUserPlanes.View.Refresh()
        End If
    End Sub

    Private Sub flightsFilter_Click(sender As Object, e As RoutedEventArgs)
        viewFlights.View.Refresh()
    End Sub
    Private Sub bookingsFilter_Click(sender As Object, e As RoutedEventArgs)
        viewBookings.View.Refresh()
    End Sub
    Private Sub modelTextBoxPlanes_TextChanged(sender As Object, e As TextChangedEventArgs)
        viewPlanes.View.Refresh()
    End Sub
    Private Sub nameTextBoxLandingFields_TextChanged(sender As Object, e As TextChangedEventArgs)
        viewLandingFields.View.Refresh()
    End Sub
    Private Sub nameTextBoxUsers_TextChanged(sender As Object, e As TextChangedEventArgs)
        viewUsers.View.Refresh()
    End Sub

    Private Sub viewFlights_Filter(sender As Object, e As FilterEventArgs)
        Dim flight As flights = CType(e.Item, flights)

        If (flight.pilot.Equals(user.id) Or user.UserTypes.type.Equals("Controller")) And (flight.Planes.landingField.Equals(user.landingField) Or flight.pilot.Equals(user.id)) Then
            If fromDatePickerFlight.SelectedDate Is Nothing And toDatePickerFlight.SelectedDate Is Nothing Then
                e.Accepted = True
            ElseIf flight.departureDate >= fromDatePickerFlight.SelectedDate And toDatePickerFlight.SelectedDate Is Nothing Then
                e.Accepted = True
            ElseIf fromDatePickerFlight.SelectedDate Is Nothing And flight.arrivalDate <= toDatePickerFlight.SelectedDate Then
                e.Accepted = True
            ElseIf flight.departureDate >= fromDatePickerFlight.SelectedDate And flight.arrivalDate <= toDatePickerFlight.SelectedDate Then
                e.Accepted = True
            Else
                e.Accepted = False
            End If
        Else
            e.Accepted = False
        End If
    End Sub
    Private Sub viewBookings_Filter(sender As Object, e As FilterEventArgs)
        Dim booking As bookings = CType(e.Item, bookings)

        If (booking.Planes.landingField.Equals(user.landingField) Or booking.Planes.owner.Equals(user.id) Or booking.pilot.Equals(user.id)) And (booking.Planes.owner.Equals(user.id) Or booking.pilot.Equals(user.id) Or user.UserTypes.type.Equals("Controller") Or user.UserTypes.type.Equals("Teacher")) Then
            If fromDatePickerBooking.SelectedDate Is Nothing And toDatePickerBooking.SelectedDate Is Nothing Then
                e.Accepted = True
            ElseIf booking.date >= fromDatePickerBooking.SelectedDate And toDatePickerBooking.SelectedDate Is Nothing Then
                e.Accepted = True
            ElseIf fromDatePickerBooking.SelectedDate Is Nothing And booking.date <= toDatePickerBooking.SelectedDate Then
                e.Accepted = True
            ElseIf booking.date >= fromDatePickerBooking.SelectedDate And booking.date <= toDatePickerBooking.SelectedDate Then
                e.Accepted = True
            Else
                e.Accepted = False
            End If
        Else
            e.Accepted = False
        End If
    End Sub
    Private Sub viewPlanes_Filter(sender As Object, e As FilterEventArgs)
        Dim plane As planes = CType(e.Item, planes)

        If plane.landingField.Equals(user.landingField) Or plane.owner.Equals(user.id) Then
            If modelTextBoxPlanes.Text Is Nothing Then
                e.Accepted = True
            ElseIf plane.model.ToLower.Contains(modelTextBoxPlanes.Text.ToLower) Then
                e.Accepted = True
            Else
                e.Accepted = False
            End If
        Else
            e.Accepted = False
        End If
    End Sub
    Private Sub viewLandingFields_Filter(sender As Object, e As FilterEventArgs)
        Dim landingField As landingFields = CType(e.Item, landingFields)

        If nameTextBoxLandingFields.Text Is Nothing Then
            e.Accepted = True
        ElseIf landingField.name.ToLower.Contains(nameTextBoxLandingFields.Text.ToLower) Then
            e.Accepted = True
        Else
            e.Accepted = False
        End If
    End Sub
    Private Sub viewUsers_Filter(sender As Object, e As FilterEventArgs)
        Dim user As users = CType(e.Item, users)

        If (user.landingField.Equals(Me.user.landingField)) Then
            If nameTextBoxUsers.Text Is Nothing Then
                e.Accepted = True
            ElseIf user.name.ToLower.Contains(nameTextBoxUsers.Text.ToLower) Then
                e.Accepted = True
            Else
                e.Accepted = False
            End If
        Else
            e.Accepted = False
        End If
    End Sub
    Private Sub viewUserPlanes_Filter(sender As Object, e As FilterEventArgs)
        Dim plane As planes = CType(e.Item, planes)

        If plane.owner.Equals(user.id) Then
            e.Accepted = True
        Else
            e.Accepted = False
        End If
    End Sub

    Private Sub addFlightButton_Click(sender As Object, e As RoutedEventArgs)
        Dim flightDialog = New FlightDialog(user)
        flightDialog.ShowDialog()
    End Sub
    Private Sub updateFlightButton_Click(sender As Object, e As RoutedEventArgs)
        Dim flight As flights = CType(flightsDataGrid.SelectedItem, flights)
        Dim flightDialog = New FlightDialog(user, flight)
        flightDialog.ShowDialog()
    End Sub
    Private Sub deleteFlightButton_Click(sender As Object, e As RoutedEventArgs)
        Dim flight As flights = CType(flightsDataGrid.SelectedItem, flights)
        If (flight IsNot Nothing) Then
            Dim result = MessageBox.Show("Are you sure you want to delete " & flight.ArrivalField.name & " - " & flight.DepartureField.name & " flight?", "Delete flight", MessageBoxButton.YesNo, MessageBoxImage.Question)
            If (result = MessageBoxResult.Yes) Then
                bd.flights.Remove(flight)
                bd.SaveChanges()
                MsgBox("Flight deleted succesfully", MsgBoxStyle.Information)
            End If
        End If
    End Sub

    Private Sub addBookingButton_Click(sender As Object, e As RoutedEventArgs)
        Dim bookingDialog = New BookingDialog(user)
        bookingDialog.ShowDialog()
    End Sub
    Private Sub updateBookingButton_Click(sender As Object, e As RoutedEventArgs)
        Dim booking As bookings = CType(bookingsDataGrid.SelectedItem, bookings)
        Dim bookingDialog = New BookingDialog(user, booking)
        bookingDialog.ShowDialog()
    End Sub
    Private Sub deleteBookingButton_Click(sender As Object, e As RoutedEventArgs)
        Dim booking As bookings = CType(bookingsDataGrid.SelectedItem, bookings)
        If (booking IsNot Nothing) Then
            If (booking.pilot.Equals(user.id) Or user.UserTypes.type.Equals("Controller")) Then
                Dim result = MessageBox.Show("Are you sure you want to delete " & booking.date & " - " & booking.Planes.plate & " booking?", "Delete booking", MessageBoxButton.YesNo, MessageBoxImage.Question)
                If (result = MessageBoxResult.Yes) Then
                    bd.bookings.Remove(booking)
                    bd.SaveChanges()
                    MsgBox("Booking deleted succesfully", MsgBoxStyle.Information)
                End If
            Else
                MsgBox("You can't deleting a booking from other person", MsgBoxStyle.Critical)
            End If
        End If
    End Sub

    Private Sub addPlaneButton_Click(sender As Object, e As RoutedEventArgs)
        Dim planeDialog = New PlaneDialog(user)
        planeDialog.ShowDialog()
    End Sub
    Private Sub updatePlaneButton_Click(sender As Object, e As RoutedEventArgs)
        Dim plane As planes = CType(planesDataGrid.SelectedItem, planes)
        Dim planeDialog = New PlaneDialog(user, plane)
        planeDialog.ShowDialog()
    End Sub
    Private Sub deletePlaneButton_Click(sender As Object, e As RoutedEventArgs)
        Dim plane As planes = CType(planesDataGrid.SelectedItem, planes)
        If (plane IsNot Nothing) Then
            Dim result = MessageBox.Show("Are you sure you want to delete " & plane.plate & " - " & plane.model & " plane?", "Delete plane", MessageBoxButton.YesNo, MessageBoxImage.Question)
            If (result = MessageBoxResult.Yes) Then
                bd.planes.Remove(plane)
                bd.SaveChanges()
                MsgBox("Plane deleted succesfully", MsgBoxStyle.Information)
            End If
        End If
    End Sub

    Private Sub addLandingFieldButton_Click(sender As Object, e As RoutedEventArgs)
        Dim landingFieldDialog = New LandingFieldDialog(user)
        landingFieldDialog.ShowDialog()
    End Sub
    Private Sub updateLandingFieldButton_Click(sender As Object, e As RoutedEventArgs)
        Dim landingField As landingFields = CType(landingFieldsDataGrid.SelectedItem, landingFields)
        Dim landingFieldDialog = New LandingFieldDialog(user, landingField)
        landingFieldDialog.ShowDialog()
    End Sub
    Private Sub deleteLandingFieldButton_Click(sender As Object, e As RoutedEventArgs)
        Dim landingField As landingFields = CType(landingFieldsDataGrid.SelectedItem, landingFields)
        If (landingField IsNot Nothing) Then
            Dim result = MessageBox.Show("Are you sure you want to delete " & landingField.name & " landingField?", "Delete landingField", MessageBoxButton.YesNo, MessageBoxImage.Question)
            If (result = MessageBoxResult.Yes) Then
                bd.landingFields.Remove(landingField)
                bd.SaveChanges()
                MsgBox("LandingField deleted succesfully", MsgBoxStyle.Information)
            End If
        End If
    End Sub

    Private Sub addUserButton_Click(sender As Object, e As RoutedEventArgs)
        Dim userDialog = New UserDialog(user)
        userDialog.ShowDialog()
    End Sub
    Private Sub updateUserButton_Click(sender As Object, e As RoutedEventArgs)
        Dim user As users = CType(usersDataGrid.SelectedItem, users)
        Dim userDialog = New UserDialog(Me.user, user)
        userDialog.ShowDialog()
    End Sub
    Private Sub deleteUserButton_Click(sender As Object, e As RoutedEventArgs)
        Dim user As users = CType(UsersDataGrid.SelectedItem, users)
        If (user IsNot Nothing) Then
            Dim result = MessageBox.Show("Are you sure you want to delete " & user.name & " - " & user.lastname & " user?", "Delete user", MessageBoxButton.YesNo, MessageBoxImage.Question)
            If (result = MessageBoxResult.Yes) Then
                bd.users.Remove(user)
                bd.SaveChanges()
                MsgBox("User deleted succesfully", MsgBoxStyle.Information)
            End If
        End If
    End Sub

    Private Sub addUserPlaneButton_Click(sender As Object, e As RoutedEventArgs)
        Dim planeDialog = New PlaneDialog(user)
        planeDialog.ShowDialog()
    End Sub
    Private Sub updateUserPlaneButton_Click(sender As Object, e As RoutedEventArgs)
        Dim plane As planes = CType(userPlanesDataGrid.SelectedItem, planes)
        Dim planeDialog = New PlaneDialog(user, plane)
        planeDialog.ShowDialog()
    End Sub
    Private Sub deleteUserPlaneButton_Click(sender As Object, e As RoutedEventArgs)
        Dim plane As planes = CType(userPlanesDataGrid.SelectedItem, planes)
        If (plane IsNot Nothing) Then
            Dim result = MessageBox.Show("Are you sure you want to delete " & plane.plate & " - " & plane.model & " plane?", "Delete plane", MessageBoxButton.YesNo, MessageBoxImage.Question)
            If (result = MessageBoxResult.Yes) Then
                bd.planes.Remove(plane)
                bd.SaveChanges()
                MsgBox("Plane deleted succesfully", MsgBoxStyle.Information)
            End If
        End If
    End Sub

    Private Sub saveProfileButton_Click(sender As Object, e As RoutedEventArgs)
        If (Not nameTextBox.Text.Equals("") And Not lastnameTextBox.Text.Equals("") And landingFieldComboBox.SelectedItem IsNot Nothing And Not usernameTextBox.Text.Equals("") And Not passwordPasswordBox.Password.Equals("")) Then
            Dim result = MessageBox.Show("Are you sure you want to update your profile?", "Update profile", MessageBoxButton.YesNo, MessageBoxImage.Question)
            If (result = MessageBoxResult.Yes) Then
                bd.users.Find(user.id).name = nameTextBox.Text
                bd.users.Find(user.id).lastname = lastnameTextBox.Text
                bd.users.Find(user.id).landingField = CType(landingFieldComboBox.SelectedItem, landingFields).id
                bd.users.Find(user.id).username = usernameTextBox.Text
                bd.users.Find(user.id).password = passwordPasswordBox.Password
                bd.SaveChanges()
                MsgBox("Profile succesfully updated", MsgBoxStyle.Information)
            End If
        Else
            MsgBox("You can't leave an empty field", MsgBoxStyle.Exclamation)
        End If
    End Sub

    ''' <summary>
    ''' Método para evitar que el SelectionChanged del TabControl se ejecute junto al del DataGrid
    ''' </summary>
    ''' <param name="sender"></param>
    ''' <param name="e"></param>
    Private Sub SelectionChanged(sender As Object, e As SelectionChangedEventArgs)
        e.Handled = True
    End Sub
End Class
