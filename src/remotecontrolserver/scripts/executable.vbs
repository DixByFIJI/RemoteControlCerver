Dim WShell
Set WShell = CreateObject("WScript.Shell")
Set objArgs = WScript.Arguments
WShell.Run objArgs(0), 0
Set WShell = Nothing
