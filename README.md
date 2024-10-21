# Iterated Prisoner's Dilemma Solver
Iterated Prisoner's Dilemma solver created as part of the Computational Intelligence Techniques course during my master's degree
-
In the Iterated Prisoner's Dilemma (IPD) two rational players (agents, prisoners) are being simultaneously questioned by the police. Each of the players can either collaborate or defect, and for each action they receive a specified number of points.

**The points are awarded as follows:**
- if both players A and B cooperated, they receive 3 points each,
- if player A defected and player B cooperated, player A receives 5 points and player B gets 0,
- if player A cooperated and player B defected, player A receives 0 points and player B gets 5,
- if both players A and B betrayed, they receive 1 point each.

The goal of the players is to receive the biggest amount of points.

The IPD is solved using two main approaches:
- **the Genetic / Evolutionary Algorithm**:
  - it operates on populations of potential solutions, referred to as individuals, with subsequent populations created based on previous ones using selection, crossover, and mutation operators,
  - each generation is better than the previous one because, according to the idea of evolution, the best-adapted individuals have the best chances of creating offspring.
- **Particle Swarm Optimization** adapted to binary representation:
  - it operates on a swarm of particles, each representing a potential solution,
  - the particles move in subsequent iterations based on their current location and velocity,
  - the velocity of the particles is calculated based on the particles' best location and the best location of the neighbouring particles,
  - the global neighbourhood scheme is used - each particle is adjacent to every other particle.
