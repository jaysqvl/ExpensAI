There are 2 distinct cloud run services with 3 distinct possible endpoints

1. text-service which handles any text input requests
    a. This would be the summary endpoint and the insight endpoint
2. vision-service which handles any vision based input requests
    a. This would be the camera request which should automatically generate transactions in the app based on images
    b. The vision service will use base64 image encoding so as to avoid having to use manage a cloud storage/bucket solution

Both of these services will be configured on GCP as follows
1. inline editor
2. vCPU only alloc'd during request
3. Separate functions to double free tier vCPU time