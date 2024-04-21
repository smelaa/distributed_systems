from fastapi import FastAPI, Request, Form, HTTPException
from fastapi.responses import HTMLResponse
from fastapi.staticfiles import StaticFiles
from fastapi.templating import Jinja2Templates
import requests
import aiohttp
from API_KEYS import API_NINJAS_KEY

app=FastAPI()
app.mount("/static", StaticFiles(directory="static"), name="static")
templates = Jinja2Templates(directory="templates")

def is_daytime(sunrise_sunset_data, current_time):
    def get_sunrise_sunset_min(key):
        key_split=sunrise_sunset_data[key].split(":")
        key_min=int(key_split[0])*60+int(key_split[1])
        if key_split[2].split(" ")[1]=="PM": key_min+=12*60
        return key_min

    try:
        sunset_min=get_sunrise_sunset_min("sunset")
        sunrise_min=get_sunrise_sunset_min("sunrise")

        current_split=current_time.split(":")
        minutes=int(current_split[1])
        hours=int(current_split[0])
        if minutes>60 or minutes<0 or hours>24 or hours<0:
            raise ValueError
        current_min=hours*60+minutes
    except ValueError as e:
        raise HTTPException(
                status_code=500, detail=f"We cannot parse provided time data!"
        )

    return current_min>sunrise_min and current_min<sunset_min

@app.exception_handler(HTTPException)
async def http_exception_handler(request: Request, exception: HTTPException):
    status_code = exception.status_code
    detail = exception.detail

    return templates.TemplateResponse(
        "error.html",
        {
            "request": request,
            "status_code": status_code,
            "detail": detail,
        },
        status_code=status_code,
    )

@app.get("/", response_class=HTMLResponse)
async def homepage(request: Request):
    return templates.TemplateResponse("homepage.html", {"request": request})

@app.post("/results", response_class=HTMLResponse)
async def post_results(request: Request, city:str = Form(None), current_time:str = Form(None)):
    if not city or not current_time:
        return templates.TemplateResponse(
            "homepage.html", {"request": request, "error": "PROVIDE CORRECT INFORMATION"}
        )
    
    try:
        geocoding_response = requests.get(url="https://api.api-ninjas.com/v1/geocoding", params={"city": city}, headers={'X-Api-Key': API_NINJAS_KEY})
        geocoding_response= geocoding_response.json()

        if (len(geocoding_response)==0):
            raise HTTPException(
                status_code=404, detail=f"We cannot find city with the provided name!"
            )
        
        geocoding_data = {"latitude":geocoding_response[0]["latitude"], "longitude":geocoding_response[0]["longitude"], "city":geocoding_response[0]["name"]}
    except Exception as e:
        raise HTTPException(
            status_code=500, detail=f"Failed to fetch or parse GeocodingAPI data: {e}"
        )
    
    try:
        sunrise_sunset_response = requests.get(url="https://api.sunrise-sunset.org/json", params={"lat": geocoding_data["latitude"], "lng": geocoding_data["longitude"]})
        sunrise_sunset_response= sunrise_sunset_response.json()
        sunrise_sunset_data = {"sunrise":sunrise_sunset_response["results"]["sunrise"], "sunset":sunrise_sunset_response["results"]["sunset"]}
    except Exception as e:
        raise HTTPException(
            status_code=500, detail=f"Failed to fetch or parse Sunrise-sunset data: {e}"
        )
    
    to_print=""
    for_today=""
    daytime=""
    task=""

    if is_daytime(sunrise_sunset_data, current_time):
        daytime="It's a daytime!"
        async with aiohttp.ClientSession() as session:
            async with session.get(url="https://api.api-ninjas.com/v1/jokes", params={"limit": 1}, headers={'X-Api-Key': API_NINJAS_KEY}) as jokes_response:
                try:
                    jokes_response= await jokes_response.json()
                    to_print = jokes_response[0]["joke"]
                    for_today="Joke for today"
                except Exception as e:
                    raise HTTPException(
                        status_code=500, detail=f"Failed to fetch or parse Jokes data: {e}"
                    )
            
            async with session.get(url="https://api.api-ninjas.com/v1/bucketlist", headers={'X-Api-Key': API_NINJAS_KEY}) as bucketlist_response:
                try:
                    bucketlist_response= await bucketlist_response.json()
                    task = bucketlist_response["item"]
                except Exception as e:
                    raise HTTPException(
                        status_code=500, detail=f"Failed to fetch or parse Bucketlist data: {e}"
                    )
    else:
        daytime="It's a nightime!"
        task="Go to sleep. Have a good night!"
        try:
            stoic_response = requests.get(url="https://stoic.tekloon.net/stoic-quote")
            stoic_response= stoic_response.json()
            print(stoic_response)
            to_print = stoic_response["quote"]+"\t~"+stoic_response["author"]
            for_today="Stoic quote for today"
        except Exception as e:
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
        "daytime": daytime,
        "task": task
        }
    return templates.TemplateResponse("results.html", context=data)