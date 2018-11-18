<?php
putenv("HOME=/");

function getRoutePointsFromHERE($lat1,$lon1,$lat2,$lon2)
{
    $req = 'https://route.api.here.com/routing/7.2/calculateroute.xml?app_id=8MjVb7liRiSs2lgwa7tE&app_code=WqFBNSAzOaPX7pEr-R8SNg&waypoint0=geo!'.$lat1.'%2C'.$lon1.'&waypoint1=geo!'.$lat2.'%2C'.$lon2.'&mode=fastest;car;traffic:disabled;motorway:-2';
    $xml = simplexml_load_file($req);
    $xmlLeg = $xml->Response->Route->Leg;
    $points = array();
    foreach($xmlLeg->Maneuver as $item)
        $points[] = array((string)$item->Position->Latitude,(string)$item->Position->Longitude, (string) $item->Length);
    return $points;
}

function get5SensorsFromAirly($lat,$lon)
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
    $req1 = 'https://airapi.airly.eu/v2/installations/nearest?lat='.$lat.'&lng='.$lon.'&maxDistanceKM=-1&maxResults=5';
    $foundSensors = json_decode(file_get_contents($req1,false,$context));
    $senMetas = array();
    foreach($foundSensors as $fSen)
    {
        $senMetas[] = array((string)$fSen->location->latitude,(string)$fSen->location->longitude,(string)$fSen->id);
    }
    $returnData=array();
    foreach ($senMetas as $senMeta)
    {
        $req2 = 'https://airapi.airly.eu/v2/measurements/installation?installationId='.$senMeta[2];
        $sensordata = json_decode(file_get_contents($req2,false,$context));
        $values = $sensordata->current->values;
        $value = $values[2]->value;
        $returnData[] = array($senMeta[0],$senMeta[1],$value);
    }
    return $returnData;
}



$returnData="";
$points = getRoutePointsFromHERE(51.5,21.4,51.5,21.42);
foreach($points as $point)
{
    $sensors = get5SensorsFromAirly($point[0], $point[1]);

    $returnData.=$point[0].','.$point[1].','.$point[2].';';
    foreach($sensors as $sensor)
        $returnData.=$sensor[0].','.$sensor[1].','.$sensor[2].',';
    $returnData = substr($returnData,0,strlen($returnData)-1).";";
}
echo $returnData;
file_put_contents("smogitude_input.csv",$returnData);

$output="";
exec('python smogitude.py3',$output);
for($i = 0; $i < count($output); $i++) {
    echo $output[$i];
    echo "<br>";
}

?>
