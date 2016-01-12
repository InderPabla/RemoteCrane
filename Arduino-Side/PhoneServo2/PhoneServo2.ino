#include <Servo.h>
/**
 *    y
 *    |
 *    |____ x
 *   /
 *  z
 */

Servo joint_1;  // base joint - z axis rotation
int joint_angle_1 = 0;
Servo joint_2;  // frist arm joint - y axis rotation
int joint_angle_2 = 0;
Servo joint_3;  // second arm joint - y axis rotation
int joint_angle_3 = 0;
Servo joint_4;  // clamp rotation joint - x axis rotation 
int joint_angle_4 = 0;
Servo joint_5;  // servo used to control the clamp, 0 degrees = closed 
int joint_angle_5 = 0;

int pos = 0;    // variable to store the servo position
bool mode = false;
void setup() 
{
  //joint 3, up = 0 degree, bottom = 180 degree. Move down motion.
  joint_3.attach(4);
  joint_3.write(0);delay(1200);
  joint_angle_3 = 0;

  //joint 2, bottom = 0 degree, up = 180. Move up motion.
  joint_2.attach(3);
  joint_2.write(0);delay(1200);
  joint_angle_2 = 0;

  //joint 1, -> facing = 0 degree. <- facing = 180 degree. Counter clockwise motion.
  joint_1.attach(2);  
  joint_1.write(0);delay(1200);
  joint_angle_1 = 0;

  //joint 4, 
  joint_4.attach(5);
  joint_4.write(90);
  joint_angle_4 = 90;

  //joint 5, 0 degree = closed.
  joint_5.attach(6);
  joint_5.write(35);
  joint_angle_5 = 35;

  
  Serial.begin(9600);
  //Serial.println("Starting");
}

void transitionJoint(Servo joint,int to,int motorNumber)
{
  byte diff = 0;
  int from = 0;
  switch(motorNumber)
  {
    case 1: from = joint_angle_1;
    break;
    case 2: from = joint_angle_2;
    break;
    case 3: from = joint_angle_3;
    break;
    case 4: from = joint_angle_4;
    break;
    case 5: from = joint_angle_5;
    break;
    default:
    break;
  }
    
  if(to!=from)
  {
    if(from>to)
    {
      diff = from-to; 
      byte counter = 0;
      while(counter<=diff)
      {
        counter++;
        joint.write(from-counter);
        delay(15);
      }
    }  
    else if(from<to)
    {
      diff = to-from;
      byte counter = 0;
      while(counter<=diff)
      {
        counter++;
        joint.write(from+counter);
        delay(15);
      }
    }
  }

  switch(motorNumber)
  {
    case 1: joint_angle_1 = to;
    break;
    case 2: joint_angle_2 = to;
    break;
    case 3: joint_angle_3 = to;
    break;
    case 4: joint_angle_4 = to;
    break;
    case 5: joint_angle_5 = to;
    break;
    default:
    break;
  }
}

void loop() 
{
   int turn_1 = 0;
   int turn_2 = 0;
   int turn_3 = 0;
   int turn_4 = 0;
   int turn_5 = 0;
   bool isAvailable = false;
   byte byte_counter = 0;

   if(Serial.available()>0)
   {
     while (byte_counter<5) 
     {
          if(Serial.available()>0)
          {
            byte data = Serial.read();
            Serial.println(data);
            switch(byte_counter)
            {
              case 0: turn_1 = data;
              break;
              case 1: turn_2 = data;
              break;
              case 2: turn_3 = data;
              break;
              case 3: turn_4 = data;
              break;
              case 4: turn_5 = data;
              break;
              default:
              break;
            }
            byte_counter++;
          }
      }
      isAvailable = true;
    }
    
    if(isAvailable)
    {
      transitionJoint(joint_1,turn_1,1);
      delay(100);
      transitionJoint(joint_2,turn_2,2);
      delay(100);
      transitionJoint(joint_3,turn_3,3);
      delay(100);
      transitionJoint(joint_4,turn_4,4);
      delay(100);
      transitionJoint(joint_5,turn_5,5);
      delay(100);
      Serial.flush();
    }  
}
