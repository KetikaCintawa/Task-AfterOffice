Feature: End to End simulation test

Scenario: As a user I can add new data
    Given A list of objects are available
    When I add a new object to the etalase
    Then The object is available

Scenario Outline: As a user I can add new data with some data 
    Given A list of objects are available
    When I add a new "<payload>" to etalase
    Then The object is available

Examples:
    |payload    |
    |addObject  |
    |addObject2 |

# Scenario Outline: As a user I can update data
#     Given A list of objects are available
#     When I add a new "<payload>" to the API
#     And The object is available
#     Then I can update object "<update>"
    
# Examples:
#     |payload    | update        | 
#     |addObject  | updateObject  |
#     |addObject2 | updateObject2 |