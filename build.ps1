<#
.SYNOPSIS
    Build and packaging tool for Among Us 3D
.PARAMETER CreateSetup
    Run Inno Setup compiler after build to create an installation wizard
#>

[CmdletBinding()]
param (
    [Parameter()]
    [switch]$CreateSetup
)

begin {
    $setupDirectory = Join-Path $PSScriptRoot "release"
    
    function CreateJreBundle () {
        Start-Process -FilePath "jlink.exe" -ArgumentList `
            "--no-header-files","--no-man-pages","--compress=2","--strip-debug",`
            "--add-modules","java.base,java.logging,java.sql,java.desktop,jdk.unsupported",`
            "--output",".\among-us-3d-release\jre" `
            -WorkingDirectory $setupDirectory -NoNewWindow -Wait
    }

    function InvokeLaunch4j () {
        $l4j = Join-Path ${env:ProgramFiles(x86)} "Launch4j"
        $l4jj = Join-Path $l4j "launch4j.jar"
        $l4jc = Join-Path $setupDirectory "l4j.xml"
        if (Test-Path -Path $l4jj -PathType Leaf) {
            # The wrapper launch4jc.exe does not support other runtimes than Java SE
            Start-Process -FilePath "java.exe" -ArgumentList "-jar","`"$l4jj`"","`"$l4jc`"" -WorkingDirectory $setupDirectory -NoNewWindow -Wait
        } else {
            Write-Error "Launch4j was not found at $l4j"
            exit 1
        }
    }

    function InvokeInnoSetup () {
        $is = Join-Path ${env:ProgramFiles(x86)} "Inno Setup 6"
        $iscc = Join-Path $is "ISCC.exe"
        $iss = Join-Path $setupDirectory "setup.iss"
        if (Test-Path -Path $iscc -PathType Leaf) {
            Start-Process -FilePath $iscc -ArgumentList "`"$iss`"" -WorkingDirectory $setupDirectory -NoNewWindow -Wait
        } else {
            Write-Error "Inno Setup was not found at $is"
            exit 1
        }
    }
}

process {
    $ErrorActionPreference = "Stop"

    $ultralight = Join-Path $PSScriptRoot "assets\Natives\Ultralight"

    if (!(Test-Path -Path $ultralight -PathType Container)) {
        Write-Error "Ultralight binaries were not found at $ultralight"
        exit 1
    }

    # Start-Process -FilePath ".\gradlew.bat" -ArgumentList "jar","--no-daemon" -NoNewWindow -Wait

    if ($CreateSetup) {
        CreateJreBundle
        InvokeLaunch4j
        InvokeInnoSetup
    }
}
