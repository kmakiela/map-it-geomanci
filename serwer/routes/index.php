<?php
putenv("HOME=/");

function getRouteDataFromHERE($lat1,$lon1,$lat2,$lon2)
{
    $req = 'https://route.api.here.com/routing/7.2/calculateroute.xml?app_id=8MjVb7liRiSs2lgwa7tE&app_code=WqFBNSAzOaPX7pEr-R8SNg&waypoint0=geo!'.$lat1.'%2C'.$lon1.'&waypoint1=geo!'.$lat2.'%2C'.$lon2.'&mode=fastest;car;traffic:disabled;motorway:-2';
    $xml = simplexml_load_file($req);
    $xmlLeg = $xml->Response->Route->Leg;
    $points = array();
    foreach($xmlLeg->Maneuver as $item)
        $points[] = array((string)$item->Position->Latitude,(string)$item->Position->Longitude, (float) $item->Length);
    return array($points,(int)$xmlLeg->Length,(int)$xmlLeg->TravelTime);
}

function get5SensorsFromCachedAirly($lat,$lon)
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
        $cached_sensor_data = json_decode(file_get_contents('saved_sensor_data.json'), true);
        $value = $cached_sensor_data[$senMeta[2]];
        $returnData[] = array($senMeta[0],$senMeta[1],$value);
    }
    return $returnData;
}

function d($lat1,$lon1,$lat2,$lon2)
{
    return sqrt(pow($lat1-$lat2,2)+pow($lon1-$lon2,2));
}

function calculateRouteAttr($lat1,$lon1,$lat2,$lon2)
{
    $HERE = getRouteDataFromHERE($lat1,$lon1,$lat2,$lon2);
    $points=$HERE[0];
    $points_wAvg=array();
    foreach($points as $point)
    {
        $lat=$point[0];
        $len=$point[1];
        $sensors = get5SensorsFromCachedAirly($lat,$len);

        $weightedSum = 0;
        $weights =0;

        foreach ($sensors as $sensor)
        {
            $d=d($lat,$len,$sensor[0],$sensor[1]);
            if($d!=0)$ratio=(1/$d);
            else
            {
                $weightedSum=$sensor[2];
                $weights=1;
                break;
            }
            $weightedSum += $ratio*$sensor[2];
            $weights +=$ratio;
        }
        $average=$weightedSum/$weights;
        $points_wAvg[] = array($point[2],$average);
    }
    $smogitude=0.0;
    for ($i=0;$i<count($points_wAvg)-1;$i++)
    {
        $smogitude += (($points_wAvg[$i][1] + $points_wAvg[$i + 1][1]) / 2) * $points_wAvg[$i][0];
    }
    return array('length'=>$HERE[1],'time'=>$HERE[2],'smogitude'=>$smogitude);
}

//fetchAirlyData();

//var_dump(calculateRouteAttr(51.5,21.4,51.5,21.43));
var_dump (calculateRouteAttr(50.060865, 19.791230,50.060865, 19.991230));
//var_dump calculateRouteAttr(50.229878, 19.074875,50.060865, 19.991230);

?>
