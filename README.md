# DigitalOcean Droplet Setup Documentation

## Overview
This document outlines the steps to migrate your setup from an old DigitalOcean droplet to a new one. Follow the instructions carefully to ensure a smooth migration process.

---

## Steps to Migrate to the New Droplet

### 1. Create a New Droplet
- Provision a new droplet for the API backend with the necessary resources (CPU, memory, storage, etc.) based on your application's requirements.

---

### 2. Install Java
1. Install the required Java version (currently using Java 21):
    ```bash
    sudo apt install oracle-java21-installer -y
    sudo apt install openjdk-21-jdk -y
    ```
2. Verify the installation:
    ```bash
    java --version
    ```

---

### 3. Install and Configure Redis
1. Follow the [DigitalOcean guide](https://www.digitalocean.com/community/tutorials/how-to-install-and-secure-redis-on-ubuntu-20-04) to install and secure Redis.
2. Copy existing configuration files from the old droplet (`/etc/redis/` directory) to the new droplet. Ensure these include necessary settings like authentication and other custom configurations.

---

### 4. Copy SystemD Files
- Copy the necessary `SystemD` service files (e.g., `/etc/systemd/system`) from the old droplet to the new droplet. This includes files such as `webapp.service` and any other user-defined daemon files.

---

### 5. Transfer Files
1. From the root directory of the old droplet, move all required files to the new droplet:
    - Exclude unwanted files (e.g., old log files) during the transfer.
2. Set appropriate permissions for the transferred files, if necessary.

---

### 6. Update Certificates
- Determine whether your application uses:
  - **Domain-based certificates:** Update the DNS records with the new droplet’s IP to repoint the domain.
  - **IP-based certificates:** Generate a new certificate for the new IP and update all applications accordingly.

---

### 7. Configure and Start SystemD Daemons
1. Enable and start the required services:
    ```bash
    sudo systemctl enable integrationadapter.service
    sudo systemctl start complianceregistry.service
    ```
2. Repeat the above commands for other services as needed.

---

## Notes
- Ensure all necessary dependencies are installed on the new droplet.
- Perform thorough testing of all applications and services after migration.
