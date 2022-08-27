# VId
Unique, Random Id Generator.

<H2>Abstract:</H2>

Technique to generate random IDs which are 100% unique(collision free) irrespective of the hardware used(i.e. hardware with slow or fast performance doesn't matter).

As per the options/choices made by a user initially, a dynamic state of the algorithm will be generated and that will be updated upon certain conditions.

Instance on which application deployed, <br>
--> if it's stopped/crashed and user presents latest updated state given then new IDs will be unique and random than the ones generated before using that state or that state's ancestors.<br> 
--> else a new dynamic state will be generated and processed.<br>

------------------
Notes:-
 
This "trackless & storage less uniqueness" is specific to the state of algorithm/mathematics-steps used.

Combined with MAC address of NIC by IEEE these are UUID. 

Combined with node-bits/instance-bits on a particular server, these are unique across all instances in a server and in world of MAC.

Number/Frequency of state-updations is also extremely less and is -->0 with certain switches & gears (choices, options) selected by user.

Flexibilities:-

This dependency on dynamic-state-of-algorithm can be removed using appropriate switches & gears but then it would be called as minimal basic implementation because current final-minimal-version is supporting 80+ variations of this VId and this minimal-basic is not coded for this boring switch-gear combination as it's not much scalable for high performance machines.

Any number of symbols can be used, yes that's also possible without any collision and tracking other that the dynamic-updation-of-state.

Other stuffs too.

-------------------


Video Introduction:: https://youtu.be/Yq3mhSRk5Rk 

An ID generator that can generate unique and Random Ids.

No need to track already generated IDs.

Even if system fails, upon restart it will give unique random IDs again.

Currently, I have kept algo as --secret--. Along with that, version Alpha's output is being put inside a Alpha* folder.

<b>Techies, non-techies can contact me on vivek.funeesh@gmail.com, if they find output-results interesting.</b>

Future 1st final version will be 50% modified version with more randomness and amount of IDs that can be generated.

<b> Before_Insight.txt contains some of the difficulties I faced and experience I gained while understanding time & randomness.</b>

<b>EDIT 1::</b> Final Alpha Output has been attached which has astonishing results. Randomness has been increased too much.

Now little more optimization is needed along with some more randomness, load-balancing. Tracking overhead will still remain to almost-> 0.
