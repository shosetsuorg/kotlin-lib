-- This file will be later moved to the extensions repo
---
--- Created by doomsdayrs.
--- DateTime: 1/24/20 8:24 AM
---

-- Meta class
Madara = { name = "", baseURL = "", lang = "", dateFormat = "" }

function Madara:test()
    print("test")
end
function Madara:getName()
    return self.name
end
function Madara:getBaseURL()
    return self.baseURL
end
function Madara:getLang()
    return self.lang
end
function Madara:getDateFormat()
    return self.dateFormat
end

function Madara:new (o, name, baseURL, lang, dateFormat)
    o = o or {}
    setmetatable(o, self)
    self.name = name
    self.baseURL = baseURL
    self.lang = lang
    self.dateFormat = dateFormat
    return o
end

