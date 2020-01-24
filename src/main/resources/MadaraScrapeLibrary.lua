-- This file will be later moved to the extensions repo
---
--- Created by doomsdayrs.
--- DateTime: 1/24/20 8:24 AM
---

-- Meta class
Madara = { name = "", baseURL = "", lang = "", dateFormat = "" }

function test()
    print("test")
end


-- Base class method new
function Madara:new (o, name, baseURL, lang, dateFormat)
    o = o or {}
    self.name = name
    self.baseURL = baseURL
    self.lang = lang
    self.dateFormat = dateFormat
    return self
end
