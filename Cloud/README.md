## Deployment Steps

ML Model deployment in App Engine:

1.  Configure your App Engine in GCP
2.  Open Google Cloud console and activate cloud shell
3.  From the cloud shell terminal clone this repository.

    ```sh
    https://github.com/Zeventier/Penumonia-Detector.git
    ```

4.  Move to the working directory

    ```sh
    cd Penumonia-Detector
    cd Cloud
    ```

5.  In the cloud shell click open editor to review the required files to deploy to App Engine

    - main.py = The API for your machine learning model to run in the cloud. in this project we use Flask.
    - requirements.txt = This requirements.txt file is used for specifying what python packages are required to run the project you are looking at
    - app.yaml = You configure your App Engine app's settings in the app.yaml file The app.yaml file contains information about your app's code, such as the runtime and the latest version identifier.

6.  return to the cloud shell terminal and run this code to authorizes gcloud and other SDK tools to access Google Cloud Platform using your user account credentials, or from an account of your choosing whose credentials are already available and Sets up a new or existing configuration.

    ```sh
    gcloud init
    ```

7.  still in Cloud Shell terminal, to deploy our ML model to App Engine run this code.

    ```sh
    gcloud app deploy
    ```

8.  After succesful deployment, you can access the given endpoint and test the prediction using postman web version https://web.postman.co/ (POST with image)

    how to send a file on postman:

    1. After setting request method to POST, click to the 'body' tab.
    2. Select form-data. At first line, you'll see text boxes named key and value. Write 'image' to the key. You'll see value type which is set to 'text' as default. Make it File and upload your file.
    3. Click send

Troubleshooting:

- Failure status: UNKNOWN: Error Response: [4] DEADLINE_EXCEEDED
  There's a 10 minute default timeout for Docker builds (the mechanism by which runtime: custom App Engine builds work). You can increase this by running

```sh
gcloud config set app/cloud_build_timeout [NUMBER OF SECONDS]
```

- if your h5 model file size is bigger than what github can store, you can upload your model on GCP cloud shell editor and put it in in the saved_model directory

- Value error in user code
  Make sure you have the same target_size and batch_size in predict.py and the model you use (ask your ML cohort or look into ipynb file in which the model is created)
