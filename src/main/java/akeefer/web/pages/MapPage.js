var map;
var stepDisplay;
var distanceSum = 0;
var start;
var ziel;

function initialize() {
    var mapOptions = {
        center: new google.maps.LatLng(4.990000, 11.898389),
        zoom: 3,
        scaleControl: true,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    map = new google.maps.Map(document.getElementById("map_canvas"),
        mapOptions);

    start = new google.maps.Marker({
        position: new google.maps.LatLng(48.137043, 11.578571),
        map: map,
        title: 'Start Muenchen, Deutschland'
    });
    ziel = new google.maps.Marker({
        position: new google.maps.LatLng(-33.930940, 18.498131),
        map: map,
        title: 'Ziel Kapstadt, Suedafrika'
    });

    var directionsService = new google.maps.DirectionsService();
    var directionsDisplay = new google.maps.DirectionsRenderer();
    directionsDisplay.setMap(map);
    var request = {
        origin: start.getPosition(),
        destination: ziel.getPosition(),
        travelMode: google.maps.TravelMode.WALKING,
        unitSystem: google.maps.UnitSystem.METRIC,
        optimizeWaypoints: true,
        waypoints: [
            {
                location: "41.910071, 12.491023",//"Rom, Italien",
                stopover: true
            },
            {
                location: "38.840377, 16.226374", //Italien
                stopover: true
            },
            {
                location: "Trapani, Italien",
                stopover: true
            }
        ]
    };
    stepDisplay = new google.maps.InfoWindow();
    directionsService.route(request, function (response, status) {
        var warnings = document.getElementById('warnings_panel');
        if (status == google.maps.DirectionsStatus.OK) {
            warnings.innerHTML = '<b>' + response.routes[0].warnings + '</b>';
            directionsDisplay.setDirections(response);
            showSteps(response);
        } else {
            warnings.innerHTML = '<b>' + 'Fehler bei der Berechnung: ' + status + '</b>';
        }
        personenPassendZoomen();
    });
}

function showSteps(directionResult) {
    // For each step, place a marker, and add the text to the marker's
    // info window. Also attach the marker to an array so we
    // can keep track of it and remove it when calculating new
    // routes.
    // myRoute ist vom Typ DirectionsLeg
    for (var legIndex = 0; legIndex < directionResult.routes[0].legs.length; legIndex++) {
        var myRoute = directionResult.routes[0].legs[legIndex];

        for (var i = 0; i < myRoute.steps.length; i++) {
            // Typ DirectionsStep
            var directionsStepAktuell = myRoute.steps[i];
            //if (i > 0) {
            //var directionsStepVorher = myRoute.steps[i - 1];
            distanceSum = distanceSum + directionsStepAktuell.distance.value;
            for (var personIndex = 0; personIndex < personen.length; personIndex++) {
                var person = personen[personIndex];
                if (!person.done && person.distance < distanceSum) {
                    person.done = true;
                    // erstmal einen ungenaue Positionierung, die ist aber immer zu weit
                    person.location = directionsStepAktuell.start_location;
                    positionPerson(person);


                    // detailpositionierung
//                            if (null != directionsStepVorher.steps) {
//                                var distanceDetail = distanceSum - directionsStepVorher.distance.value;
//                                for (var j = 0; j < directionsStepVorher.steps.length; i++) {
//                                    var directionsStepDetail = directionsStepVorher.steps[j];
//                                    distanceDetail = distanceDetail + directionsStepDetail.distance.value;
//                                    if (person.distance < distanceDetail) {
//                                        positionPerson(person.id, directionsStepDetail.start_location, person.distance);
//                                    }
//                                }
//                            }
                }
            }
            //}
//                    var marker = new google.maps.Marker({
//                        position: directionsStepAktuell.start_location,
//                        map: map
//                    });
//                    attachInstructionText(marker, directionsStepAktuell.instructions + "\ndistanz: " + distance/1000 + "km");
        }
    }
}

function attachInstructionText(marker, text) {
    google.maps.event.addListener(marker, 'click', function () {
        // Open an info window when the marker is clicked on,
        // containing the text of the step.
        stepDisplay.setContent(text);
        stepDisplay.open(map, marker);
    });
}

function positionPerson(person) {
    // Add markers to the map

    // Marker sizes are expressed as a Size of X,Y
    // where the origin of the image (0,0) is located
    // in the top left of the image.

    // Origins, anchor positions and coordinates of the marker
    // increase in the X direction to the right and in
    // the Y direction down.
    var image = {
        url: 'images/position_' + person.id + '.png',
        // This marker is 42 pixels wide by 48 pixels tall.
        size: new google.maps.Size(42, 48),
        // The origin for this image is 0,0.
        origin: new google.maps.Point(0, 0),
        // The anchor for this image is the base of the flagpole at 0,32.
        anchor: new google.maps.Point(21, 48)
    };
    // Shapes define the clickable region of the icon.
    // The type defines an HTML &lt;area&gt; element 'poly' which
    // traces out a polygon as a series of X,Y points. The final
    // coordinate closes the poly by connecting to the first
    // coordinate.
    var shape = {
        coords: [1, 1, 1, 30, 42, 30, 42 , 1],
        type: 'poly'
    };
    var title = person.id + '@' + person.distance / 1000 + 'km';
    var personMarker = new google.maps.Marker({
        position: person.location,
        map: map,
        title: title,
        icon: image,
        shape: shape
    });
    attachInstructionText(personMarker, title);
    person.marker = personMarker;
}

function personenPassendZoomen() {
    // Heranzoomen an alle Beteiligten
    var bounds = new google.maps.LatLngBounds();
    for (var personIndex = 0; personIndex < personen.length; personIndex++) {
        var person = personen[personIndex];
        bounds.extend(person.location);
    }
    map.fitBounds(bounds);
    map.panToBounds(bounds);
}

function gesamtansicht() {
    // Heranzoomen an alle Beteiligten
    var bounds = new google.maps.LatLngBounds();
    bounds.extend(start.getPosition());
    bounds.extend(ziel.getPosition());
    map.fitBounds(bounds);
    map.panToBounds(bounds);
}