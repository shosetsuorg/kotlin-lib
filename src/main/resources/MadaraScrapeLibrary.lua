-- This file will be later moved to the extensions repo
---
--- Created by doomsdayrs.
--- DateTime: 1/24/20 8:24 AM
---

local DefaultStructure = Require("DefaultStructure").Formatter

Madara = { lang = "", dateFormat = "" }

function Madara:new (o, baseURL, identification, imageURL, name, lang, dateFormat)
    o = o or DefaultStructure:new(o, baseURL, identification, imageURL, name)
    self.lang = lang
    self.dateFormat = dateFormat
    return self
end

function Madara:test()
    print("This is MADARA")
end

function get()
    return Madara:new(nil, nil, nil, nil, nil, nil, nil)
end