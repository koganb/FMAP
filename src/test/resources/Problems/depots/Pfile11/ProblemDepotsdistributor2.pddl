(define (problem depotprob8765)
(:domain depot)
(:objects
 depot0 depot1 depot2 - depot
 distributor0 distributor1 distributor2 - distributor
 truck0 truck1 - truck
 crate0 crate1 crate2 crate3 crate4 crate5 crate6 crate7 crate8 crate9 - crate
 pallet0 pallet1 pallet2 pallet3 pallet4 pallet5 - pallet
 hoist0 hoist1 hoist2 hoist3 hoist4 hoist5 - hoist
)
(:shared-data
  (clear ?x - (either surface hoist))
  ((at ?t - truck) - place)
  ((pos ?c - crate) - (either place truck))
  ((on ?c - crate) - (either surface hoist truck)) - 
(either depot0 depot1 depot2 distributor0 distributor1 truck0 truck1)
)
(:init
 (myAgent distributor2)
 (= (pos crate0) depot1)
 (not (clear crate0))
 (= (on crate0) pallet1)
 (= (pos crate1) depot0)
 (clear crate1)
 (= (on crate1) pallet0)
 (= (pos crate2) depot2)
 (not (clear crate2))
 (= (on crate2) pallet2)
 (= (pos crate3) depot1)
 (clear crate3)
 (= (on crate3) crate0)
 (= (pos crate4) depot2)
 (not (clear crate4))
 (= (on crate4) crate2)
 (= (pos crate5) depot2)
 (not (clear crate5))
 (= (on crate5) crate4)
 (= (pos crate6) distributor2)
 (not (clear crate6))
 (= (on crate6) pallet5)
 (= (pos crate7) distributor2)
 (not (clear crate7))
 (= (on crate7) crate6)
 (= (pos crate8) distributor2)
 (clear crate8)
 (= (on crate8) crate7)
 (= (pos crate9) depot2)
 (clear crate9)
 (= (on crate9) crate5)
 (= (at truck0) depot2)
 (= (at truck1) distributor0)
 (= (located hoist0) depot0)
 (clear hoist0)
 (= (located hoist1) depot1)
 (clear hoist1)
 (= (located hoist2) depot2)
 (clear hoist2)
 (= (located hoist3) distributor0)
 (clear hoist3)
 (= (located hoist4) distributor1)
 (clear hoist4)
 (= (located hoist5) distributor2)
 (clear hoist5)
 (= (placed pallet0) depot0)
 (not (clear pallet0))
 (= (placed pallet1) depot1)
 (not (clear pallet1))
 (= (placed pallet2) depot2)
 (not (clear pallet2))
 (= (placed pallet3) distributor0)
 (clear pallet3)
 (= (placed pallet4) distributor1)
 (clear pallet4)
 (= (placed pallet5) distributor2)
 (not (clear pallet5))
)
(:global-goal (and
 (= (on crate0) crate7)
 (= (on crate1) pallet4)
 (= (on crate2) pallet5)
 (= (on crate3) crate9)
 (= (on crate4) pallet0)
 (= (on crate5) pallet2)
 (= (on crate6) crate5)
 (= (on crate7) crate1)
 (= (on crate8) pallet3)
 (= (on crate9) crate2)
))
)
