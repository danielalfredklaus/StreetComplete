package ch.uzh.ifi.accesscomplete.reports.database

enum class LoginState {
    NOTINITIATED,
    NOEMAIL,
    NOPASSWORD,
    FAILED,
    SUCCESS
}
