# UberSystem


## Project Overview

Ride Queue Manager is a Java-based project that enhances the ride-hailing process by utilizing a queue-based system for ride requests and driver assignments. Instead of assigning requests directly to drivers, requests are placed in one of four queues based on the pickup address's zone. Drivers pick up passengers from the queue corresponding to their current location, ensuring efficient ride distribution.

## Features

Queue-Based Ride Requests: Users request rides as usual, but requests are placed into one of four zone-based queues.

Zone-Based Driver Matching: Drivers pick up passengers from the queue corresponding to their current location.

Drop-off Handling: After picking up a passenger, the driver completes the ride by dropping them off at the destination.

Zone Switching: If there are no available drivers in a zone, the system allows a driver to move to another zone using the driveTo action.

## How It Works

Requesting a Ride:

- A user requests a ride with a pickup address.

- The system determines the appropriate zone based on the pickup address and places the request in the corresponding queue.

Picking Up a Passenger:

- A driver selects a ride from the queue corresponding to their current location.

- If the queue is empty, the driver may use driveTo to move to another zone with pending requests.

Completing the Ride:

- The driver drops off the passenger at the requested destination.
