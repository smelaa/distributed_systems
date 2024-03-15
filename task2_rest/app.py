from fastapi import FastAPI, Request, Form, HTTPException
from fastapi.responses import HTMLResponse
from fastapi.staticfiles import StaticFiles
from fastapi.templating import Jinja2Templates
from requests.exceptions import HTTPError, RequestException
import requests

API_NINJAS_KEY="962xfVTbMDGdZbShn3xtLg==xhJDmSDZpwimUfkL"

app=FastAPI()
app.mount("/static", StaticFiles(directory="static"), name="static")
templates = Jinja2Templates(directory="templates")

def is_daytime(sunrise_sunset_data, current_time):
    def get_sunrise_sunset_min(key):
        key_split=sunrise_sunset_data[key].split(":")
        key_min=int(key_split[0])*60+int(key_split[1])
        if key_split[2].split(" ")[1]=="PM": key_min+=12*60
        return key_min

    sunset_min=get_sunrise_sunset_min("sunset")
    sunrise_min=get_sunrise_sunset_min("sunrise")

    current_split=current_time.split(":")
    current_min=int(current_split[0])*60+int(current_split[1])

    return current_min>sunrise_min and current_min<sunset_min

@app.get("/", response_class=HTMLResponse)
async def homepage(request: Request):
    return templates.TemplateResponse("homepage.html", {"request": request})

@app.post("/results", response_class=HTMLResponse)
async def post_results(request: Request, city: str = Form(None), current_time: str = Form(None)):

    if not city or not current_time:
        return templates.TemplateResponse(
            "homepage.html", {"request": request, "error": "PROVIDE CORRECT INFORMATION"}
        )
    
    try:
        geocoding_response = requests.get(url="https://api.api-ninjas.com/v1/geocoding", params={"city": city}, headers={'X-Api-Key': API_NINJAS_KEY})
        geocoding_response.raise_for_status()
        geocoding_response= geocoding_response.json()

        if (len(geocoding_response)):
            raise HTTPException(
                status_code=500, detail=f"We cannot find city with the provided name!"
            )
        
        geocoding_data = {"latitude":geocoding_response[0]["latitude"], "longitude":geocoding_response[0]["longitude"], "city":geocoding_response[0]["name"]}
    except (KeyError, HTTPError, RequestException) as e:
        raise HTTPException(
            status_code=500, detail=f"Failed to fetch or parse GeocodingAPI data: {e}"
        )
    
    try:
        sunrise_sunset_response = requests.get(url="https://api.sunrise-sunset.org/json", params={"lat": geocoding_data["latitude"], "lng": geocoding_data["longitude"]})
        sunrise_sunset_response.raise_for_status()
        sunrise_sunset_response= sunrise_sunset_response.json()
        sunrise_sunset_data = {"sunrise":sunrise_sunset_response["results"]["sunrise"], "sunset":sunrise_sunset_response["results"]["sunset"]}
    except (KeyError, HTTPError, RequestException) as e:
        raise HTTPException(
            status_code=500, detail=f"Failed to fetch or parse Sunrise-sunset data: {e}"
        )
    
    to_print=""
    for_today=""
    daytime=""

    if is_daytime(sunrise_sunset_data, current_time):
        try:
            jokes_response = requests.get(url="https://api.api-ninjas.com/v1/jokes", params={"limit": 1}, headers={'X-Api-Key': API_NINJAS_KEY})
            jokes_response.raise_for_status()
            jokes_response= jokes_response.json()
            to_print = jokes_response[0]["joke"]
            for_today="Joke for today"
            daytime="It's a daytime!"
        except (KeyError, HTTPError, RequestException) as e:
            raise HTTPException(
                status_code=500, detail=f"Failed to fetch or parse Jokes data: {e}"
            )
    else:
        try:
            stoic_response = requests.get(url="https://stoic.tekloon.net/stoic-quote")
            stoic_response.raise_for_status()
            stoic_response= stoic_response.json()
            print(stoic_response)
            to_print = stoic_response["quote"]+"\t~"+stoic_response["author"]
            for_today="Stoic quote for today"
            daytime="It's a nightime!"
        except (KeyError, HTTPError, RequestException) as e:
            raise HTTPException(
                status_code=500, detail=f"Failed to fetch or parse Stoic quote data: {e}"
            )
        
    data = {
        "request": request, 
        "for_today": for_today, 
        "to_print": to_print, 
        "city": geocoding_data["city"], 
        "sunrise": sunrise_sunset_data["sunrise"], 
        "sunset": sunrise_sunset_data["sunset"], 
        "daytime": daytime
        }
    return templates.TemplateResponse("results.html", context=data)




    #porównaj czas
    #zrequestuj odpowiednie api
    #obsługa błędów itp