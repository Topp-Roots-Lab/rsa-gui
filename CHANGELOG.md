# Changelog

All notable changes to this project will be documented in this file.

## v4.1.1 - 2021-02-12

### Changed

- QC2 step now deletes any existing `thresholding_qc2.csv` to prevent permission eror due to re-assigning the owner of a file

## v4.1.0 - 2019-01-21

### Added

- Documentation for installation, development, and troubleshooting environment and application.
- GNOME desktop entry file for applications menu.
- Pre-compiled binaries from CentOS 6.6 Final version.
- `.gitignore` file for Java and IntelliJ.
- `pom.xml` and Maven support to manage dependencies.
- A `default.qa.properties` file that uses a QA database for development and testing.
- Placeholder application icon
- Changelog

### Changed

- Moved `desktop.properties` to `/etc/opt/rsa-gia/` to better adhere to Linux FHS.
- When using the application shortcut, logs are now written to `/var/log/rsa-gia` with the user's username and the date and time when the program was started.
- Moved `gia.log` to `/var/log/rsa-gia/`, and created symlink to it in `/opt/rsa-gia/bin/gia/`.
- Windows/frames now first appear in the center of the screen.
- Updated file paths in python-based sub-components (i.e., `rsa-tools`) and hard-coded paths in Java source code

### Removed

- Java `.class` files.
- Unused dependencies.

### Fixed

- Added missing `=` to SQL query that checked if an image type exists given a seed during data importation.
- Fixed typos
