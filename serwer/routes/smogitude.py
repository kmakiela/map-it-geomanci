import csv
import math



def main():
    resultList = []
    with open('smogitude_input.csv', newline='') as input:
        csvread = csv.reader(input, delimiter = ';')
        readFile = csvread.__next__()
        for i, row in enumerate(readFile):
            if i % 2 == 0:
                Line = row.split(',')
                if i == len(readFile)-2:
                    myLat, myLon = [float(x) for x in Line]
                else:
                    myLat, myLon, dist = [float(x) for x in Line]
                nextLine = readFile[i + 1].split(',')
                nextLine = [float(x) for x in nextLine]
                weightedSum = 0
                divider = 0
                lon=0
                lat=0
                zFlag=False
                for j, item in enumerate(nextLine):
                    if j%3==0:
                        lat = item
                    if j%3 ==1:
                        lon = item
                    if j%3==2:
                        measurement=item
                        distance = math.sqrt(
                            (myLat - lat) * (myLat - lat) + (myLon - lon) * ( myLon - lon))

                        if distance == 0:
                            resultList.append(measurement)
                            zFlag=True
                            break
                        else:
                            weightedSum += (1 / distance) * measurement
                            divider += (1 / distance)
                if(not zFlag):
                    resultList.append(weightedSum / divider)
                if i != len(readFile) - 2:
                    resultList.append(dist)

    result = 0
    for i, elem in enumerate(resultList):
        if i%2!=0:
            result+=(resultList[i-1]+resultList[i+1])/2*elem
    print(result)


if __name__ == "__main__":
    main()