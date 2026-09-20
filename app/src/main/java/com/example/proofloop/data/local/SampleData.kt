package com.example.proofloop.data.local

import com.example.proofloop.domain.models.MicroLesson
import com.example.proofloop.domain.models.Mission
import com.example.proofloop.domain.models.ProofCard
import com.example.proofloop.domain.models.UserSkill

object SampleData {
    val initialSkills = listOf(
        UserSkill(id = "skill_math", name = "Mathematical Reasoning", score = 78, previousScore = 61, targetScore = 88, category = "Mathematics"),
        UserSkill(id = "skill_calc", name = "Calculation Accuracy", score = 86, previousScore = 72, targetScore = 95, category = "Mathematics"),
        UserSkill(id = "skill_perc", name = "Percentage Reasoning", score = 55, previousScore = 55, targetScore = 75, category = "Mathematics"),
        UserSkill(id = "skill_ct", name = "Critical Thinking", score = 74, previousScore = 70, targetScore = 85, category = "Problem Solving"),
        UserSkill(id = "skill_comm", name = "Explanation & Defense", score = 81, previousScore = 78, targetScore = 90, category = "Communication")
    )

    val microLessonObservationVsAssumption = MicroLesson(
        id = "ml_percentage_impact",
        title = "Percentage Shock & Cumulative Totals",
        targetGap = "Percentage Reasoning",
        readDurationSeconds = 60,
        coreInsight = "Percentage increases compound across unit volume, creating disproportionate budget shocks.",
        takeaways = listOf(
            "Always apply price adjustments to the total unit volume (e.g. 12% on Rs 85 for 240 students = Rs 2,448 jump).",
            "Calculate non-negotiable overheads (Decoration + Transport) before absorbing variable food cost fluctuations.",
            "Formulate trade-off scenarios: either reduce unit count or cut secondary line items to keep variance under 0%."
        ),
        exercisePrompt = "Re-evaluate the festival budget: adjust the food line item by +12% and defend whether to reduce student headcount or trim decoration."
    )

    // MISSION 01 (PRIMARY DEMO SUBJECT): Campus Festival Budget
    val missionFestivalBudget = Mission(
        id = "mission_festival_budget",
        title = "Campus Festival Budget",
        domain = "FINANCIAL MATHEMATICS",
        subject = "MATHEMATICS",
        subtitle = "Can you keep a 240-student college festival strictly within Rs 12,000?",
        description = "You have a strict Rs 12,000 budget for organizing the college annual festival.",
        contextScenario = "Expected attendance: 240 students. Food quote: Rs 85/student. Decoration: Rs 3,200. Transport: Rs 1,500. Can you balance the budget and handle dynamic price increases?",
        estimatedMinutes = 5,
        difficulty = "Level 2",
        primaryCompetency = "Mathematical Reasoning",
        targetGap = "Percentage Reasoning",
        rules = listOf(
            "1. Observe key financial constraints and line items",
            "2. Calculate total expenditure across fixed & variable costs",
            "3. Reason whether the Rs 12,000 ceiling is exceeded",
            "4. Dynamic What-If: Food price surges by 12%",
            "5. Defend your trade-off adjustment"
        ),
        firstQuestion = "Identify the total variable cost vs fixed overhead. What is the baseline total expenditure?",
        interviewPrompt = "Interview the caterer or transport vendor. What did they quote as minimum commitment?",
        defendPrompt = "Give your final balanced proposal. Explain whether you reduce attendees or cut decoration to respect the Rs 12,000 cap.",
        followUpQuestion = "Food prices just surged by 12%. What is the exact new food total and how do you adapt your budget?",
        correctiveChallenge = "Calculate: (240 x 85) x 1.12 = Rs 22,848. Identify 2 specific trade-offs with exact rupee amounts to keep the deficit at zero.",
        sampleEvidenceNote = "Vendor invoice: 240 meals @ Rs 85 = Rs 20,400. Fixed overhead = Rs 4,700. Immediate deficit confirmed.",
        isMathMission = true
    )

    // MISSION 02: Canteen Pricing & Profit Margin
    val missionCanteenPricing = Mission(
        id = "mission_canteen_pricing",
        title = "Canteen Pricing & Margins",
        domain = "BUSINESS MATHEMATICS",
        subject = "MATHEMATICS",
        subtitle = "Calculate break-even volume and net profit margins under raw material inflation.",
        description = "Campus canteen daily footfall is 450 items, but net operating margin dropped from 22% to 7%.",
        contextScenario = "Raw material ingredient costs rose 18%, while retail meal combo price was held fixed at Rs 60.",
        estimatedMinutes = 5,
        difficulty = "Level 3",
        primaryCompetency = "Mathematical Reasoning",
        targetGap = "Percentage Reasoning",
        rules = listOf(
            "1. Calculate cost per plate before and after 18% ingredient hike",
            "2. Determine break-even sales volume",
            "3. Defend dynamic pricing vs portion optimization"
        ),
        firstQuestion = "What is the new cost per plate if ingredient cost was previously 65% of the Rs 60 retail price?",
        interviewPrompt = "Interview the canteen chef regarding portion waste and ingredient substitution options.",
        defendPrompt = "Propose either a 10% price revision or a 15% batch cost reduction with exact margin projections.",
        followUpQuestion = "Why would a flat Rs 10 price hike reduce total profit if student price elasticity is -1.4?",
        correctiveChallenge = "Compute the exact elasticity drop: if volume falls 14% on a 16.6% price increase, does total gross profit rise or fall?",
        sampleEvidenceNote = "Raw ingredient receipts: Cooking oil up 22%, dairy up 14%. Average wholesale basket inflation: 18.2%.",
        isMathMission = true
    )

    // MISSION 03: Room Design & Floor Area (Spatial Math + Camera)
    val missionRoomDesign = Mission(
        id = "mission_room_design",
        title = "Classroom Floor Tiling Challenge",
        domain = "GEOMETRY & ESTIMATION",
        subject = "MATHEMATICS",
        subtitle = "Scan the room environment, estimate surface area, and calculate tile wastage allowance.",
        description = "The new robotics lab requires anti-static flooring tile installation with an 8% cut wastage buffer.",
        contextScenario = "Lab dimensions: 12.5 meters length, 8.4 meters width. Tile specifications: 60cm x 60cm boxes.",
        estimatedMinutes = 6,
        difficulty = "Level 2",
        primaryCompetency = "Calculation Accuracy",
        targetGap = "Evidence Gathering",
        rules = listOf(
            "1. Scan the classroom layout with phone camera to spot perimeter obstacles",
            "2. Calculate total floor surface area in square meters",
            "3. Apply 8% buffer for cutting and perimeter corners",
            "4. Compute the exact box count required"
        ),
        firstQuestion = "What is the net floor area and how many individual 0.36 m^2 tiles are needed without waste?",
        interviewPrompt = "Ask the maintenance supervisor about structural columns or floor sockets that require tile cutouts.",
        defendPrompt = "Defend why buying 320 boxes is safer than buying exactly 292 boxes.",
        followUpQuestion = "What happens to total procurement cost if tile vendor offers a 5% discount on orders exceeding 350 boxes?",
        correctiveChallenge = "Calculate: Area = 105 m^2. With 8% buffer = 113.4 m^2. Divide by 0.36 m^2/tile to get exact tile count.",
        sampleEvidenceNote = "Camera frame snapshot: 2 structural columns (0.8m x 0.8m) identified on south wall.",
        isMathMission = true
    )

    // MISSION 04: Campus Travel & Logistics
    val missionTravelLogistics = Mission(
        id = "mission_travel_logistics",
        title = "Inter-Campus Shuttle Optimization",
        domain = "KINEMATICS & COST",
        subject = "MATHEMATICS",
        subtitle = "Optimize speed, departure frequency, and fuel expenditure across 3 campus routes.",
        description = "Three 32-seater electric campus vans run 14 km round trips with differing passenger wait times.",
        contextScenario = "Students wait 24 minutes during morning peak. Increasing van speed from 25 km/h to 35 km/h reduces round-trip cycle by 8 minutes.",
        estimatedMinutes = 5,
        difficulty = "Level 4",
        primaryCompetency = "Mathematical Reasoning",
        targetGap = "Decision Making",
        rules = listOf(
            "1. Calculate cycle time at current 25 km/h vs 35 km/h",
            "2. Calculate battery depletion rate increase (energy scales with speed^1.4)",
            "3. Defend optimal fleet frequency schedule"
        ),
        firstQuestion = "How many total passenger trips per hour can 3 vans deliver at 25 km/h vs 35 km/h?",
        interviewPrompt = "Interview a waiting student at North Gate regarding willingness to pay express shuttle surcharge.",
        defendPrompt = "Propose an energy-efficient timetable that caps peak wait time under 10 minutes.",
        followUpQuestion = "If charging tariff is 40% cheaper between 11 AM - 2 PM, how should the battery swap cycle be scheduled?",
        correctiveChallenge = "Formulate the exact time-saving formula: T = Distance / Speed + Fixed Dwell Time.",
        sampleEvidenceNote = "GPS tracker log: Peak dwell time at library stop is 3.5 minutes per loop.",
        isMathMission = true
    )

    // MISSION 05: Cafeteria Queue (Preserved Demonstration Core Scenario)
    val missionCafeteriaQueue = Mission(
        id = "mission_cafeteria_queue",
        title = "Cafeteria Queue Investigation",
        domain = "PRODUCT & QUEUE SYSTEMS",
        subject = "CRITICAL THINKING",
        subtitle = "Find the root problem behind a 20-minute campus queue.",
        description = "Your college cafeteria has a 20-minute lunch queue.",
        contextScenario = "You have 5 minutes to investigate why students are delayed every day during peak hours.",
        estimatedMinutes = 5,
        difficulty = "Intermediate",
        primaryCompetency = "Root-Cause Reasoning",
        targetGap = "Evidence Gathering",
        rules = listOf(
            "1. Observe the environment with camera",
            "2. Interview someone affected with microphone",
            "3. Formulate 2 testable hypotheses",
            "4. Defend your root-cause solution"
        ),
        firstQuestion = "What would you investigate first?",
        interviewPrompt = "Interview someone affected by the problem. What did you learn?",
        defendPrompt = "Give one solution and explain why it addresses the root cause.",
        followUpQuestion = "Why wouldn't simply adding another cashier solve the root problem?",
        correctiveChallenge = "Before choosing a solution, identify 2 possible causes and explain how you would test each.",
        sampleEvidenceNote = "Queue halts before cashier; 3 consecutive students experienced payment terminal latency.",
        isMathMission = false
    )

    val allMissions = listOf(
        missionFestivalBudget,
        missionCanteenPricing,
        missionRoomDesign,
        missionTravelLogistics,
        missionCafeteriaQueue
    )

    val defaultMission = missionFestivalBudget

    // Believable quick-fill demo suggestions for Festival Budget:
    val sampleAnswerInvestigate = "Fixed overhead: Decoration (Rs 3,200) + Transport (Rs 1,500) = Rs 4,700. Variable food: 240 students x Rs 85 = Rs 20,400. Total expected: Rs 25,100, exceeding Rs 12,000 by Rs 13,100."
    val sampleAnswerInterview = "The student festival lead stated: 'We cannot compromise on student count, but the caterer agreed that buffet style instead of packed meals cuts per-head cost to Rs 45.'"
    val sampleAnswerSolution = "Renegotiate catering to Rs 45/student buffet (Rs 10,800), reduce decoration to Rs 1,000 digital banners, and share transport costs with the sports club (Rs 200), totaling Rs 12,000."
    val sampleAnswerFollowUp = "If food increases by 12% to Rs 50.40/student (Rs 12,096 total food), the Rs 96 deficit can be absorbed by eliminating printed flyers and moving 100% to WhatsApp event passes."
    val sampleAnswerCorrective = "Initial math overlooked the 12% food shock on 240 attendees (+Rs 1,296). With 240 students, each 5% food hike adds Rs 540. The robust model reserves a 10% contingency buffer."

    val initialProofCard = ProofCard(
        id = "proof_math_level4",
        title = "MATHEMATICAL REASONING",
        domain = "Applied Mathematics & Resource Optimization",
        level = "LEVEL 4",
        overallScore = 78,
        maxScore = 100,
        applicationScore = 86,
        reasoningScore = 78,
        communicationScore = 84,
        attemptsCount = 12,
        evidencePointsCount = 23,
        defendedDecisionsCount = 8,
        improvementPercent = 17,
        baselineScore = 61,
        verifiedScore = 78,
        strengths = listOf(
            "Constraint decomposition",
            "Multi-variable budgeting",
            "Percentage variance modeling"
        ),
        growthArea = "Percentage reasoning under inflation",
        evidenceList = listOf(
            "Calculated exact baseline total expenditure across fixed & variable line items",
            "Conducted caterer quote validation and identified per-unit margin buffers",
            "Simulated 12% dynamic food price shock across 240 attendees",
            "Formulated 2 balanced trade-off models without deficit",
            "Defended mathematical justification against superficial budget slashing"
        ),
        verificationHash = "PL-2026-IQOO-MATH-9941X",
        issuedDate = "September 2026",
        verificationMessage = "ProofLoop measures demonstrated capability, not just course completion."
    )
}
