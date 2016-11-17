package com.jesus.opencms.util

def values = ["1", "2", "3", "4", "5"]
def val;

print values.find{
    it.equals("100")
}