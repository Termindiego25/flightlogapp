Imports System.Data.Entity
Public Class UserDialog
    Private bd As flightlogappEntities
    Private user As users
    Private user2 As users

    Public Sub New(user As users)
        InitializeComponent()
        Me.user = user
        newWindow()
        landingFieldsComboBox.SelectedItem = bd.landingFields.Find(user.landingField)
        userTypesComboBox.SelectedItem = bd.userTypes.First()
    End Sub
    Public Sub New(user As users, user2 As users)
        InitializeComponent()
        Me.user = user
        Me.user2 = user2
        newWindow()

        nameTextBox.Text = user2.name
        lastnameTextBox.Text = user2.lastname
        userTypesComboBox.SelectedItem = bd.userTypes.Find(user2.userType)
        landingFieldsComboBox.SelectedItem = bd.landingFields.Find(user2.landingField)
        usernameTextBox.Text = user2.username
        passwordPasswordBox.Password = user2.password
    End Sub
    Sub newWindow()
        bd = New flightlogappEntities
        bd.landingFields.Load()
        bd.userTypes.Load()

        landingFieldsComboBox.ItemsSource = bd.landingFields.Local
        userTypesComboBox.ItemsSource = bd.userTypes.Local
    End Sub

    Private Sub sendUserButton_Click(sender As Object, e As RoutedEventArgs)
        If (user2 Is Nothing) Then
            user2 = New users
            If (Not nameTextBox.Text.Equals("")) Then
                user2.name = nameTextBox.Text
            Else
                MessageBox.Show("Please provide a valid name", "Invalid name", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            If (Not lastnameTextBox.Text.Equals("")) Then
                user2.lastname = lastnameTextBox.Text
            Else
                MessageBox.Show("Please provide a valid lastname", "Invalid lastname", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            user2.userType = CType(userTypesComboBox.SelectedItem, userTypes).id
            user2.landingField = CType(landingFieldsComboBox.SelectedItem, landingFields).id
            If (Not usernameTextBox.Text.Equals("")) Then
                user2.username = usernameTextBox.Text
            Else
                MessageBox.Show("Please provide a valid username", "Invalid username", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            If (Not passwordPasswordBox.Password.Equals("")) Then
                user2.password = passwordPasswordBox.Password
            Else
                MessageBox.Show("Please provide a valid password", "Invalid password", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If

            Dim result = MessageBox.Show("Are you sure you want to add " & user2.name & " " & user2.lastname & " user?", "Add new user", MessageBoxButton.YesNo, MessageBoxImage.Question)
            If (result = MessageBoxResult.Yes) Then
                bd.users.Add(user2)
                bd.SaveChanges()
                MsgBox("User succesfully added", MsgBoxStyle.Information)
                Close()
            End If
        ElseIf (user2.id) Then
            If (Not nameTextBox.Text.Equals("")) Then
                user2.name = nameTextBox.Text
            Else
                MessageBox.Show("Please provide a valid name", "Invalid name", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            If (Not lastnameTextBox.Text.Equals("")) Then
                user2.lastname = lastnameTextBox.Text
            Else
                MessageBox.Show("Please provide a valid lastname", "Invalid lastname", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If

            If (Not usernameTextBox.Text.Equals("")) Then
                user2.username = usernameTextBox.Text
            Else
                MessageBox.Show("Please provide a valid username", "Invalid username", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            If (Not passwordPasswordBox.Password.Equals("")) Then
                user2.password = passwordPasswordBox.Password
            Else
                MessageBox.Show("Please provide a valid password", "Invalid password", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If

            Dim result = MessageBox.Show("Are you sure you want to update " & user2.name & " " & user2.lastname & " user", "Update user", MessageBoxButton.YesNo, MessageBoxImage.Question)
            If (result = MessageBoxResult.Yes) Then
                bd.SaveChanges()
                MsgBox("User succesfully updated", MsgBoxStyle.Information)
                Close()
            End If
        Else
            user2 = New users
            If (Not nameTextBox.Text.Equals("")) Then
                user2.name = nameTextBox.Text
            Else
                MessageBox.Show("Please provide a valid name", "Invalid name", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            If (Not lastnameTextBox.Text.Equals("")) Then
                user2.lastname = lastnameTextBox.Text
            Else
                MessageBox.Show("Please provide a valid lastname", "Invalid lastname", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            user2.userType = CType(userTypesComboBox.SelectedItem, userTypes).id
            user2.landingField = CType(landingFieldsComboBox.SelectedItem, landingFields).id
            If (Not usernameTextBox.Text.Equals("")) Then
                user2.username = usernameTextBox.Text
            Else
                MessageBox.Show("Please provide a valid username", "Invalid username", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If
            If (Not passwordPasswordBox.Password.Equals("")) Then
                user2.password = passwordPasswordBox.Password
            Else
                MessageBox.Show("Please provide a valid password", "Invalid password", MessageBoxButton.OK, MessageBoxImage.Error)
                Exit Sub
            End If

            Dim result = MessageBox.Show("Are you sure you want to add " & user2.name & " " & user2.lastname & " user?", "Add new user", MessageBoxButton.YesNo, MessageBoxImage.Question)
            If (result = MessageBoxResult.Yes) Then
                bd.users.Add(user2)
                bd.SaveChanges()
                MsgBox("User succesfully added", MsgBoxStyle.Information)
                Close()
            End If
        End If
    End Sub
End Class
