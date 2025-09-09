Imports System.Data.Entity
Public Class LandingFieldDialog
    Private bd As flightlogappEntities
    Private user As users
    Private landingField As landingFields

    Public Sub New(user As users)
        InitializeComponent()
        bd = New flightlogappEntities
        Me.user = user
    End Sub
    Public Sub New(user As users, landingField As landingFields)
        InitializeComponent()
        bd = New flightlogappEntities
        Me.user = user
        Me.landingField = landingField

        nameTextBox.Text = landingField.name
        acronymTextBox.text = landingField.acronym
    End Sub

    Private Sub sendLandingFieldButton_Click(sender As Object, e As RoutedEventArgs)
        If (landingField Is Nothing) Then
            landingField = New landingFields
            If (Not nameTextBox.Text.Equals("")) Then
                landingField.name = nameTextBox.Text
            Else
                MessageBox.Show("Please provide a valid name", "Invalid name", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            landingField.acronym = acronymTextBox.Text

            Dim result = MessageBox.Show("Are you sure you want to add " & landingField.name & " landing field?", "Add new landing field", MessageBoxButton.YesNo, MessageBoxImage.Question)
            If (result = MessageBoxResult.Yes) Then
                bd.landingFields.Add(landingField)
                bd.SaveChanges()
                MsgBox("Landing field succesfully added", MsgBoxStyle.Information)
            End If
        ElseIf (landingField.id) Then
            If (Not nameTextBox.Text.Equals("")) Then
                landingField.name = nameTextBox.text
            Else
                MessageBox.Show("Please provide a valid name", "Invalid name", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            landingField.acronym = acronymTextBox.text

            Dim result = MessageBox.Show("Are you sure you want to update " & landingField.name & " landing field?", "Update landing field", MessageBoxButton.YesNo, MessageBoxImage.Question)
            If (result = MessageBoxResult.Yes) Then
                bd.SaveChanges()
                MsgBox("Landing field succesfully updated", MsgBoxStyle.Information)
                Close()
            End If
        Else
            landingField = New landingFields
            If (Not nameTextBox.Text.Equals("")) Then
                landingField.name = nameTextBox.Text
            Else
                MessageBox.Show("Please provide a valid name", "Invalid name", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            landingField.acronym = acronymTextBox.Text

            Dim result = MessageBox.Show("Are you sure you want to add " & landingField.name & " landing field?", "Add new landing field", MessageBoxButton.YesNo, MessageBoxImage.Question)
            If (result = MessageBoxResult.Yes) Then
                bd.landingFields.Add(landingField)
                bd.SaveChanges()
                MsgBox("Landing field succesfully added", MsgBoxStyle.Information)
                Close()
            End If
        End If
    End Sub
End Class
