<?php

function fetchAirlyData()
{
    $apikey="8diOeIk73yDuFZTe4ruo9CjGRDUFx4Br";
    $heads = array(
        'http'=>array(
            'method'=>"GET",
            'header'=>"Accept: application/json\r\n" .
                "apikey: ".$apikey
        )
    );
    $context = stream_context_create($heads);
    $req1 = 'https://airapi.airly.eu/v2/installations/nearest?lat=50.060593&lng=19.938399&maxDistanceKM=10&maxResults=-1';
    $foundSensors = json_decode(file_get_contents($req1,false,$context));
    $senMetas = array();
    foreach($foundSensors as $fSen)
    {
        $senMetas[] = (string)$fSen->id;
    }
    $sensorData=array();
    foreach ($senMetas as $senMeta)
    {
        $req2 = 'https://airapi.airly.eu/v2/measurements/installation?installationId='.$senMeta;
        $sensordata = json_decode(file_get_contents($req2,false,$context));
        $values = $sensordata->current->values;
        $value = $values[2]->value;   //PM10
        if($value==NULL) $value=80;
        $sensorData[$senMeta] = $value;
    }
    var_dump($sensorData);
    file_put_contents("saved_sensor_data.json",json_encode($sensorData));
}

fetchAirlyData();