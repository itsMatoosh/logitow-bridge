//
//  mac.cpp
//  mac
//
//  Created by Mateusz Rębacz on 25.12.2017.
//  Copyright © 2017 LOGITOW. All rights reserved.
//

#include <iostream>
#include "mac.hpp"
#include "macPriv.hpp"

void mac::HelloWorld(const char * s)
{
    macPriv *theObj = new macPriv;
    theObj->HelloWorldPriv(s);
    delete theObj;
};

void macPriv::HelloWorldPriv(const char * s) 
{
    std::cout << s << std::endl;
};

