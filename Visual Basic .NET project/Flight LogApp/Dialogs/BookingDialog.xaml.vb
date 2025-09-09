Imports System.Data.Entity
Public Class BookingDialog
    Private bd As flightlogappEntities
    Private user As users
    Private booking As bookings

    Public Sub New(user As users)
        InitializeComponent()
        Me.user = user
        newWindow()
        pilotComboBox.SelectedItem = bd.users.Find(user.id)
    End Sub
    Public Sub New(user As users, booking As bookings)
        InitializeComponent()
        Me.user = user
        Me.booking = booking
        newWindow()

        dateDatePicker.SelectedDate = booking.date.Date
        timeTimePicker.Value = Date.Parse(booking.date.TimeOfDay.ToString)
        planeComboBox.SelectedItem = bd.planes.Find(booking.plane)
        pilotComboBox.SelectedItem = bd.users.Find(booking.pilot)
        durationTimePicker.Value = Date.Parse(booking.duration.ToString)
    End Sub
    Sub newWindow()
        bd = New flightlogappEntities
        bd.planes.Load()
        bd.users.Load()

        planeComboBox.ItemsSource = bd.planes.Local
        pilotComboBox.ItemsSource = bd.users.Local
        If (user.UserTypes.type.Equals("Controller")) Then
            pilotComboBox.IsEnabled = True
        End If
    End Sub

    Private Sub sendBookingButton_Click(sender As Object, e As RoutedEventArgs)
        Dim duration As TimeSpan
        Dim bookingDate As DateTime
        If (booking Is Nothing) Then
            booking = New bookings
            If (DateTime.TryParse(dateDatePicker.SelectedDate & " " & timeTimePicker.Text, bookingDate)) Then
                booking.date = bookingDate
            Else
                MessageBox.Show("Please provide a valid date", "Invalid date", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            If (TimeSpan.TryParse(durationTimePicker.Text, duration)) Then
                booking.duration = duration
            Else
                MessageBox.Show("Please provide a valid duration format (00:00:00)", "Invalid duration format", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            If (planeComboBox.SelectedItem IsNot Nothing) Then
                booking.plane = CType(planeComboBox.SelectedItem, planes).id
            Else
                MessageBox.Show("Please provide a valid plane", "Invalid plane", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            booking.pilot = CType(pilotComboBox.SelectedItem, users).id

            Dim result = MessageBox.Show("Are you sure you want to add " & bd.planes.Find(booking.plane).plate & " - " & bd.planes.Find(booking.plane).model & " booking?", "Add new booking", MessageBoxButton.YesNo, MessageBoxImage.Question)
            If (result = MessageBoxResult.Yes) Then
                bd.bookings.Add(booking)
                bd.SaveChanges()
                MsgBox("Booking succesfully added", MsgBoxStyle.Information)
                Close()
            End If
        ElseIf (booking.id) Then
            If (DateTime.TryParse(dateDatePicker.SelectedDate & " " & timeTimePicker.Text, bookingDate)) Then
                booking.date = bookingDate
            Else
                MessageBox.Show("Please provide a valid date", "Invalid date", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            If (TimeSpan.TryParse(durationTimePicker.Text, duration)) Then
                booking.duration = duration
            Else
                MessageBox.Show("Please provide a valid duration format (00:00:00)", "Invalid duration format", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            If (planeComboBox.SelectedItem IsNot Nothing) Then
                booking.plane = CType(planeComboBox.SelectedItem, planes).id
            Else
                MessageBox.Show("Please provide a valid plane", "Invalid plane", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            booking.pilot = CType(pilotComboBox.SelectedItem, users).id

            Dim result = MessageBox.Show("Are you sure you want to update " & bd.planes.Find(booking.plane).plate & " - " & bd.planes.Find(booking.plane).model & " booking?", "Update booking", MessageBoxButton.YesNo, MessageBoxImage.Question)
            If (result = MessageBoxResult.Yes) Then
                bd.SaveChanges()
                MsgBox("Booking succesfully updated", MsgBoxStyle.Information)
                Close()
            End If
        Else
            booking = New bookings
            If (DateTime.TryParse(dateDatePicker.SelectedDate & " " & timeTimePicker.Text, bookingDate)) Then
                booking.date = bookingDate
            Else
                MessageBox.Show("Please provide a valid date", "Invalid date", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            If (TimeSpan.TryParse(durationTimePicker.Text, duration)) Then
                booking.duration = duration
            Else
                MessageBox.Show("Please provide a valid duration format (00:00:00)", "Invalid duration format", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            If (planeComboBox.SelectedItem IsNot Nothing) Then
                booking.plane = CType(planeComboBox.SelectedItem, planes).id
            Else
                MessageBox.Show("Please provide a valid plane", "Invalid plane", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            booking.pilot = CType(pilotComboBox.SelectedItem, users).id

            Dim result = MessageBox.Show("Are you sure you want to add " & bd.planes.Find(booking.plane).plate & " - " & bd.planes.Find(booking.plane).model & " booking?", "Add new booking", MessageBoxButton.YesNo, MessageBoxImage.Question)
            If (result = MessageBoxResult.Yes) Then
                bd.bookings.Add(booking)
                bd.SaveChanges()
                MsgBox("Booking succesfully added", MsgBoxStyle.Information)
                Close()
            End If
        End If
    End Sub
End Class
