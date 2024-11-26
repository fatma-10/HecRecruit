let map;
let marker;

export function initMap() {
  const defaultLocation = { lat: 40.7128, lng: -74.0060 };

  map = new google.maps.Map(document.getElementById("map"), {
    zoom: 13,
    center: defaultLocation,
    mapTypeControl: false,
  });

  marker = new google.maps.Marker({
    map: map,
    position: defaultLocation,
  });
}