<#
.SYNOPSIS
    Build and packaging tool for Among Us 3D
.PARAMETER NoInstaller
    Do not run Launch4j and Inno Setup compiler after build
#>

[CmdletBinding()]
param (
    [Parameter()]
    [switch]$NoInstaller
)

begin {
    $setupDirectory = Join-Path $PSScriptRoot "release"
    
    function GetSoftwareRoot () {
        $user = ${env:SOFTWARE_HOME}
        $sys = ${env:ProgramFiles(x86)}
        if (!$user) {
            return $sys
        } else {
            return $user
        }
    }

    function CreateJreBundle () {
        $javaHome = ${env:JAVA_HOME}
        $jlinkPath = 'jlink.exe'
        if ($javaHome) {
            $javaBin = Join-Path $javaHome "bin"
            $jlinkPath = Join-Path $javaBin $jlinkPath
        }

        Start-Process -FilePath $jlinkPath -ArgumentList `
            "--no-header-files","--no-man-pages","--compress=2","--strip-debug",`
            "--add-modules","java.base,java.logging,java.sql,java.desktop,jdk.unsupported",`
            "--output",".\among-us-3d-release\jre" `
            -WorkingDirectory $setupDirectory -NoNewWindow -Wait
    }

    function InvokeLaunch4j () {
        $swRoot = GetSoftwareRoot
        $l4j = Join-Path $swRoot "Launch4j"
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
        $swRoot = GetSoftwareRoot
        $is = Join-Path $swRoot "Inno Setup 6"
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

    $ultralight = Join-Path $PSScriptRoot "assets\natives\ultralight"

    if (!(Test-Path -Path $ultralight -PathType Container)) {
        Write-Error "Ultralight binaries were not found at $ultralight"
        exit 1
    }

    Write-Host "Building jar file..."
    iex '.\gradlew.bat jar --no-daemon'

    if (!$NoInstaller) {
        Write-Host "Creating JRE bundle..."
        CreateJreBundle

        Write-Host "Creating executable..."
        InvokeLaunch4j

        Write-Host "Creating installer..."
        InvokeInnoSetup
    }
}
