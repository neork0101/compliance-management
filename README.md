
# droplet-linux-initservice-details

## volunteer management initservice file placed at 
```bash
/etc/systemd/system myapp.service
```
## jar file need to be place at 
```bash
/root/webapp
```
## Common `systemctl` Commands

### Start a Service:
```bash
sudo systemctl start service_name
```
This command starts a service immediately.

### Stop a Service:
```bash
sudo systemctl stop service_name
```
This stops a running service.

### Restart a Service:
```bash
sudo systemctl restart service_name
```
This stops and then starts a service again.

### Enable a Service:
```bash
sudo systemctl enable service_name
```
This command ensures that the service starts automatically when the system boots.

### Disable a Service:
```bash
sudo systemctl disable service_name
```
This prevents the service from starting automatically on boot.

### Check the Status of a Service:
```bash
sudo systemctl status service_name
```
This provides detailed information about the service’s current status, whether it's running, failed, or inactive.

### Reloading the `systemd` Configuration:
```bash
sudo systemctl daemon-reload
```
This reloads the `systemd` configuration files. It’s necessary after you make changes to service files.

## Viewing Logs

### View Logs Like `cat`:
```bash
sudo journalctl -u myapp.service
```

### View Logs in Follow Mode:
```bash
sudo journalctl -u myapp.service -f
```

### View Logs Since Bootup:
```bash
sudo journalctl -u myapp.service -b
```

### View Logs for a Specific Time Frame:
```bash
sudo journalctl -u myapp.service --since "YYYY-MM-DD HH:MM:SS" --until "YYYY-MM-DD HH:MM:SS"
```

### Filter Logs by Priority (e.g., Error, Warning, Info):
```bash
sudo journalctl -u myapp.service -p err
```

### View the Most Recent Logs:
```bash
sudo journalctl -u myapp.service -n 100
```
