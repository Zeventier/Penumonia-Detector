from flask import Flask, request
from flask_cors import CORS, cross_origin
from saved_model import predict
import json

app = Flask(__name__)
CORS(app, resources=r'/*')
@app.route('/')
def index():
    return "Pneumonix is Running"

@app.route('/predict', methods=['GET','POST'])
def predict_images():
    

    data = request.files.get("file")
    if data == None:
        return 'No data received'
    else:
        prediction = predict.predict(data)

    output={
     "pneumonia-type" : str(prediction)
    }
    return json.dumps(output)

    

if __name__ == "__main__":
    app.run(host='0.0.0.0', debug=True)