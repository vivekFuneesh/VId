
    VVId (VId), Vivek's Id or Vivek's virtual Id is a technique  or algorithm to generate 
    unique, random, trackless & storageless(no storage is required to keep track of them) IDs (strings, numbers, symbols etc.).
                  Copyright (C) 2020  Vivek Mangla
    
    Tutorials at https://youtube.com/playlist?list=PLxxkGs1eKWxQFtCn8QNpNkCrnHKXvl8bm 
    Video tutorials under creative commons licence:- https://creativecommons.org/ 

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

    To connect with author, possible way to reach is via email 
    vivek.funeesh@gmail.com 


=================
# VId
100% Unique, Random, Trackingless, Storageless Id Generator.

<H2>Abstract:</H2>

Technique to generate random IDs which are 100% unique(collision free) irrespective of the hardware used(i.e. hardware with slow or fast performance doesn't matter).

As per the options/choices made by a user initially, a dynamic state of the algorithm will be generated and that will be updated upon certain conditions.

Instance on which application deployed, <br>
--> if it's stopped/crashed and user presents latest updated state given then new IDs will be unique and random than the ones generated before using that state or that state's ancestors.<br> 
--> else a new dynamic state will be generated and processed.<br>

------------------
<H2>Notes:-</H2>

<H3>This is a sample code:-</H3> Scholars/researchers can use this as a core reference. It has all possible extensible open-sockets, means that if anyone wants to complete extensions, complex-level-1 and complex-level2 then code-design has already sockets open to do that.

To optimize code for usability-requirements of merely few years, use long instead of BigInteger. 

<a href="https://youtu.be/Yq3mhSRk5Rk ">Video Introduction Over Here</a> 

<a href="https://youtube.com/playlist?list=PLxxkGs1eKWxQFtCn8QNpNkCrnHKXvl8bm">Video Tutorials (language - local hindi with english mix) here</a>

To convert tutorials into pdf, use speech-to-text engine available by various vendors.

<H3>Other Notes</H3>

This "trackless & storage less uniqueness" is specific to the state of algorithm/mathematics-steps used.

Combined with MAC address of NIC by IEEE these are UUID. 

Combined with node-bits/instance-bits on a particular server, these are unique across all instances in a server and in world of MAC.

Number/Frequency of state-updations is also extremely less and is -->0 with certain switches & gears (choices, options) selected by user.

Flexibilities:-

This dependency on dynamic-state-of-algorithm can be removed using appropriate switches & gears but then it would be called as minimal basic implementation because current final-minimal-version is supporting 80+ variations of this VId and this minimal-basic is not coded for this boring switch-gear combination as it's not much scalable for high performance machines.

Any number of symbols can be used, yes that's also possible without any collision and tracking other that the dynamic-updation-of-state.

Other stuffs too... mentioned in <a href="https://youtube.com/playlist?list=PLxxkGs1eKWxQFtCn8QNpNkCrnHKXvl8bm">video tutorials</a>

-------------------

Version Alpha's output of the year 2020 is being put inside Alpha* folder.

