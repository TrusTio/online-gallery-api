# Personal Gallery - live demo: https://gallery-rest-api.herokuapp.com/
**Note: After opening the api demo, you can test it with the react frontend demo: 
https://online-gallery-react.herokuapp.com/**

## Description
This is a personal gallery REST API that lets users:

- Create and login into personal accounts 
- Create, rename, delete galleries
- Upload, rename, delete images in those galleries
- Retrieve images

## Installation
1. Edit your database information(if needed) and add your username and password.
in src/main/resources/application.properties
2. Run the application using the GalleryApplication class.
3. Wait for it to boot, and you're ready to access the exposed endpoints.
4. Navigate to localhost:8080/swagger-ui.html to see the exposed paths/endpoints by the API.

## Technical information
The project uses the Spring boot framework. It uses JWT tokens for authorization and authentication.
All data except the images is stored in the database, images are stored on the local storage.
