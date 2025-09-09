Imports System.Data.Entity
Public Class login
    Private bd As flightlogappEntities
    Friend appForm

    Public Sub New()
        InitializeComponent()
        bd = New flightlogappEntities
    End Sub
    Private Sub login_Click(sender As Object, e As RoutedEventArgs)
        bd.users.Load()
        For Each user As users In bd.users.Local
            If user.username.Equals(usernameTextBox.Text) Then
                If (user.password.Equals(passwordPasswordBox.Password)) Then
                    appForm = New MainWindow(user)
                    Close()
                    appForm.ShowDialog()
                Else
                    errorTextBlock.Visibility = Visibility.Visible
                    Exit For
                End If
            End If
        Next
        errorTextBlock.Visibility = Visibility.Visible
    End Sub

    Private Sub editLogin()
        errorTextBlock.Visibility = Visibility.Collapsed
    End Sub
End Class
