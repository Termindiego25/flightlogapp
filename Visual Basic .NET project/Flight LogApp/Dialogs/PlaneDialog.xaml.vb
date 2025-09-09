Imports System.Data.Entity
Public Class PlaneDialog
    Private bd As flightlogappEntities
    Private user As users
    Private plane As planes

    Public Sub New(user As users)
        InitializeComponent()
        Me.user = user
        newWindow()
        ownerComboBox.SelectedItem = bd.users.Find(user.id)
        landingFieldComboBox.SelectedItem = bd.landingFields.Find(user.landingField)
    End Sub
    Public Sub New(user As users, plane As planes)
        InitializeComponent()
        Me.user = user
        Me.plane = plane
        newWindow()

        plateTextBox.Text = plane.plate
        modelTextBox.Text = plane.model
        ownerComboBox.SelectedItem = bd.users.Find(plane.owner)
        landingFieldComboBox.SelectedItem = bd.landingFields.Find(plane.landingField)
    End Sub
    Sub newWindow()
        bd = New flightlogappEntities
        bd.users.Load()
        bd.landingFields.Load()

        ownerComboBox.ItemsSource = bd.users.Local
        landingFieldComboBox.ItemsSource = bd.landingFields.Local
        If (user.UserTypes.type.Equals("Controller")) Then
            landingFieldComboBox.IsEnabled = True
            ownerComboBox.IsEnabled = True
        End If
    End Sub

    Private Sub sendPlaneButton_Click(sender As Object, e As RoutedEventArgs)
        If (plane Is Nothing) Then
            plane = New planes
            If (Not plateTextBox.Text.Equals("")) Then
                plane.plate = plateTextBox.Text
            Else
                MessageBox.Show("Please provide a valid plate", "Invalid plate", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            If (Not modelTextBox.Text.Equals("")) Then
                plane.model = modelTextBox.Text
            Else
                MessageBox.Show("Please provide a valid model", "Invalid model", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            plane.owner = CType(ownerComboBox.SelectedItem, users).id
            plane.landingField = CType(landingFieldComboBox.SelectedItem, landingFields).id

            Dim result = MessageBox.Show("Are you sure you want to add " & plane.plate & " - " & plane.model & " plane?", "Add new plane", MessageBoxButton.YesNo, MessageBoxImage.Question)
            If (result = MessageBoxResult.Yes) Then
                bd.planes.Add(plane)
                bd.SaveChanges()
                MsgBox("Plane succesfully added", MsgBoxStyle.Information)
                Close()
            End If
        ElseIf (plane.id) Then
            If (Not plateTextBox.Text.Equals("")) Then
                plane.plate = plateTextBox.Text
            Else
                MessageBox.Show("Please provide a valid plate", "Invalid plate", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            If (Not modelTextBox.Text.Equals("")) Then
                plane.model = modelTextBox.Text
            Else
                MessageBox.Show("Please provide a valid model", "Invalid model", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            plane.owner = CType(ownerComboBox.SelectedItem, users).id
            plane.landingField = CType(landingFieldComboBox.SelectedItem, landingFields).id

            Dim result = MessageBox.Show("Are you sure you want to update " & plane.plate & " - " & plane.model & " plane?", "Update plane", MessageBoxButton.YesNo, MessageBoxImage.Question)
            If (result = MessageBoxResult.Yes) Then
                bd.SaveChanges()
                MsgBox("Plane succesfully updated", MsgBoxStyle.Information)
                Close()
            End If
        Else
            plane = New planes
            If (Not plateTextBox.Text.Equals("")) Then
                plane.plate = plateTextBox.Text
            Else
                MessageBox.Show("Please provide a valid plate", "Invalid plate", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            If (Not modelTextBox.Text.Equals("")) Then
                plane.model = modelTextBox.Text
            Else
                MessageBox.Show("Please provide a valid model", "Invalid model", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            plane.owner = CType(ownerComboBox.SelectedItem, users).id
            plane.landingField = CType(landingFieldComboBox.SelectedItem, landingFields).id

            Dim result = MessageBox.Show("Are you sure you want to add " & plane.plate & " - " & plane.model & " plane?", "Add new plane", MessageBoxButton.YesNo, MessageBoxImage.Question)
            If (result = MessageBoxResult.Yes) Then
                bd.planes.Add(plane)
                bd.SaveChanges()
                MsgBox("Plane succesfully added", MsgBoxStyle.Information)
                Close()
            End If
        End If
    End Sub
End Class
