import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Container,
  Typography,
  TextField,
  Button,
  Grid,
  Card,
  CardContent,
  Chip,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Slider,
  FormControlLabel,
  Switch,
  Alert,
  IconButton,
  Tooltip
} from '@mui/material';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import AddIcon from '@mui/icons-material/Add';
import RemoveIcon from '@mui/icons-material/Remove';
import { api, endpoints } from '../api/client';
import { format, differenceInDays, addDays } from 'date-fns';

interface Country {
  countryCode: string;
  countryName: string;
  region: string;
}

interface TripDay {
  date: Date;
  countries: string[];
}

const TripPlanner: React.FC = () => {
  const navigate = useNavigate();
  const [countries, setCountries] = useState<Country[]>([]);
  const [startDate, setStartDate] = useState<Date | null>(new Date());
  const [endDate, setEndDate] = useState<Date | null>(addDays(new Date(), 7));
  const [tripDays, setTripDays] = useState<TripDay[]>([]);
  const [userId, setUserId] = useState<number>(1001);
  const [profile, setProfile] = useState({
    avgDailyMb: 600,
    avgDailyMin: 10,
    avgDailySms: 2
  });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchCatalog();
  }, []);

  useEffect(() => {
    if (startDate && endDate) {
      generateTripDays();
    }
  }, [startDate, endDate]);

  const fetchCatalog = async () => {
    try {
      const response = await api.get(endpoints.catalog);
      setCountries(response.data.countries || []);
    } catch (error) {
      console.error('Error fetching catalog:', error);
    }
  };

  const generateTripDays = () => {
    if (!startDate || !endDate) return;
    
    const days = differenceInDays(endDate, startDate) + 1;
    const newTripDays: TripDay[] = [];
    
    for (let i = 0; i < days; i++) {
      const date = addDays(startDate, i);
      newTripDays.push({
        date,
        countries: []
      });
    }
    
    setTripDays(newTripDays);
  };

  const handleDateChange = (field: 'start' | 'end', date: Date | null) => {
    if (field === 'start') {
      setStartDate(date);
    } else {
      setEndDate(date);
    }
  };

  const handleCountryChange = (dayIndex: number, countryCode: string, isSecondCountry: boolean = false) => {
    const newTripDays = [...tripDays];
    if (isSecondCountry) {
      // Add second country
      if (!newTripDays[dayIndex].countries.includes(countryCode)) {
        newTripDays[dayIndex].countries.push(countryCode);
      }
    } else {
      // Replace first country
      newTripDays[dayIndex].countries = [countryCode];
    }
    setTripDays(newTripDays);
  };

  const removeCountry = (dayIndex: number, countryCode: string) => {
    const newTripDays = [...tripDays];
    newTripDays[dayIndex].countries = newTripDays[dayIndex].countries.filter(c => c !== countryCode);
    setTripDays(newTripDays);
  };

  const toggleSecondCountry = (dayIndex: number) => {
    const newTripDays = [...tripDays];
    if (newTripDays[dayIndex].countries.length === 1) {
      // Add empty second country slot
      newTripDays[dayIndex].countries.push('');
    } else {
      // Remove second country
      newTripDays[dayIndex].countries = newTripDays[dayIndex].countries.slice(0, 1);
    }
    setTripDays(newTripDays);
  };

  const handleSubmit = () => {
    // Validate that all days have at least one country
    const hasAllCountries = tripDays.every(day => day.countries.length > 0 && day.countries[0] !== '');
    
    if (!hasAllCountries) {
      alert('Please select at least one country for each day of your trip.');
      return;
    }

    // Convert trip days to the format expected by the backend
    const trips = tripDays.map(day => ({
      countryCode: day.countries[0],
      startDate: format(day.date, 'yyyy-MM-dd'),
      endDate: format(day.date, 'yyyy-MM-dd')
    }));

    // If there are second countries, create additional trip segments
    tripDays.forEach((day, index) => {
      if (day.countries.length > 1 && day.countries[1] !== '') {
        trips.push({
          countryCode: day.countries[1],
          startDate: format(day.date, 'yyyy-MM-dd'),
          endDate: format(day.date, 'yyyy-MM-dd')
        });
      }
    });

    const simulationRequest = {
      userId: userId,
      trips,
      profile: {
        avgDailyMb: profile.avgDailyMb,
        avgDailyMin: profile.avgDailyMin,
        avgDailySms: profile.avgDailySms
      }
    };

    navigate('/simulation', { state: { simulationRequest } });
  };

  const getCountryName = (countryCode: string) => {
    const country = countries.find(c => c.countryCode === countryCode);
    return country ? country.countryName : countryCode;
  };

  if (loading) {
    return (
      <Container maxWidth="md">
        <Typography>Loading...</Typography>
      </Container>
    );
  }

  return (
    <LocalizationProvider dateAdapter={AdapterDateFns}>
      <Container maxWidth="lg">
        <Typography variant="h4" component="h1" gutterBottom sx={{ mt: 4, mb: 3 }}>
          Plan Your Roaming Trip
        </Typography>

        {/* Trip Dates */}
        <Card sx={{ mb: 3 }}>
          <CardContent>
            <Typography variant="h6" gutterBottom>
              Trip Dates
            </Typography>
            <Grid container spacing={3}>
              <Grid size={{ xs: 12, md: 6 }}>
                <DatePicker
                  label="Start Date"
                  value={startDate}
                  onChange={(date) => handleDateChange('start', date)}
                  slotProps={{ textField: { fullWidth: true } }}
                />
              </Grid>
              <Grid size={{ xs: 12, md: 6 }}>
                <DatePicker
                  label="End Date"
                  value={endDate}
                  onChange={(date) => handleDateChange('end', date)}
                  slotProps={{ textField: { fullWidth: true } }}
                />
              </Grid>
            </Grid>
            {startDate && endDate && (
              <Typography variant="body2" color="text.secondary" sx={{ mt: 2 }}>
                Trip Duration: {differenceInDays(endDate, startDate) + 1} days
              </Typography>
            )}
          </CardContent>
        </Card>

        {/* Trip Days */}
        {tripDays.length > 0 && (
          <Card sx={{ mb: 3 }}>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Select Countries for Each Day
              </Typography>
              <Alert severity="info" sx={{ mb: 2 }}>
                Select which country (or countries) you'll be in for each day. 
                You can select up to two countries per day if you're crossing borders.
              </Alert>
              
              {tripDays.map((day, dayIndex) => (
                <Box key={dayIndex} sx={{ mb: 3, p: 2, border: '1px solid #e0e0e0', borderRadius: 1 }}>
                  <Typography variant="subtitle1" gutterBottom>
                    {format(day.date, 'EEEE, MMMM d, yyyy')}
                  </Typography>
                  
                  <Grid container spacing={2} alignItems="center">
                    {/* First Country */}
                    <Grid size={{ xs: 12, md: 4 }}>
                      <FormControl fullWidth>
                        <InputLabel>Country</InputLabel>
                        <Select
                          value={day.countries[0] || ''}
                          onChange={(e) => handleCountryChange(dayIndex, e.target.value)}
                          label="Country"
                        >
                          <MenuItem value="">
                            <em>Select a country</em>
                          </MenuItem>
                          {countries.map((country) => (
                            <MenuItem key={country.countryCode} value={country.countryCode}>
                              {country.countryName}
                            </MenuItem>
                          ))}
                        </Select>
                      </FormControl>
                    </Grid>

                    {/* Second Country Toggle */}
                    <Grid size={{ xs: 12, md: 2 }}>
                      <Button
                        variant="outlined"
                        size="small"
                        onClick={() => toggleSecondCountry(dayIndex)}
                        startIcon={day.countries.length > 1 ? <RemoveIcon /> : <AddIcon />}
                      >
                        {day.countries.length > 1 ? 'Remove' : 'Add'} Second Country
                      </Button>
                    </Grid>

                    {/* Second Country (if enabled) */}
                    {day.countries.length > 1 && (
                      <Grid size={{ xs: 12, md: 4 }}>
                        <FormControl fullWidth>
                          <InputLabel>Second Country (Optional)</InputLabel>
                          <Select
                            value={day.countries[1] || ''}
                            onChange={(e) => handleCountryChange(dayIndex, e.target.value, true)}
                            label="Second Country (Optional)"
                          >
                            <MenuItem value="">
                              <em>Select a second country</em>
                            </MenuItem>
                            {countries.map((country) => (
                              <MenuItem key={country.countryCode} value={country.countryCode}>
                                {country.countryName}
                              </MenuItem>
                            ))}
                          </Select>
                        </FormControl>
                      </Grid>
                    )}

                    {/* Selected Countries Display */}
                    <Grid size={{ xs: 12 }}>
                      <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap' }}>
                        {day.countries.filter(c => c !== '').map((countryCode, countryIndex) => (
                          <Chip
                            key={countryIndex}
                            label={getCountryName(countryCode)}
                            onDelete={() => removeCountry(dayIndex, countryCode)}
                            color="primary"
                            variant="outlined"
                          />
                        ))}
                      </Box>
                    </Grid>
                  </Grid>
                </Box>
              ))}
            </CardContent>
          </Card>
        )}

        {/* Usage Profile */}
        <Card sx={{ mb: 3 }}>
          <CardContent>
            <Typography variant="h6" gutterBottom>
              Daily Usage Profile
            </Typography>
            <Grid container spacing={3}>
              <Grid size={{ xs: 12, md: 4 }}>
                <Typography gutterBottom>Data Usage (MB/day)</Typography>
                <Slider
                  value={profile.avgDailyMb}
                  onChange={(_, value) => setProfile(prev => ({ ...prev, avgDailyMb: value as number }))}
                  min={100}
                  max={2000}
                  step={100}
                  marks={[
                    { value: 100, label: '100MB' },
                    { value: 1000, label: '1GB' },
                    { value: 2000, label: '2GB' }
                  ]}
                  valueLabelDisplay="auto"
                />
                <Typography variant="body2" color="text.secondary">
                  {profile.avgDailyMb} MB per day
                </Typography>
              </Grid>
              
              <Grid size={{ xs: 12, md: 4 }}>
                <Typography gutterBottom>Voice Calls (min/day)</Typography>
                <Slider
                  value={profile.avgDailyMin}
                  onChange={(_, value) => setProfile(prev => ({ ...prev, avgDailyMin: value as number }))}
                  min={0}
                  max={60}
                  step={5}
                  marks={[
                    { value: 0, label: '0' },
                    { value: 30, label: '30' },
                    { value: 60, label: '60' }
                  ]}
                  valueLabelDisplay="auto"
                />
                <Typography variant="body2" color="text.secondary">
                  {profile.avgDailyMin} minutes per day
                </Typography>
              </Grid>
              
              <Grid size={{ xs: 12, md: 4 }}>
                <Typography gutterBottom>SMS Messages (per day)</Typography>
                <Slider
                  value={profile.avgDailySms}
                  onChange={(_, value) => setProfile(prev => ({ ...prev, avgDailySms: value as number }))}
                  min={0}
                  max={20}
                  step={1}
                  marks={[
                    { value: 0, label: '0' },
                    { value: 10, label: '10' },
                    { value: 20, label: '20' }
                  ]}
                  valueLabelDisplay="auto"
                />
                <Typography variant="body2" color="text.secondary">
                  {profile.avgDailySms} SMS per day
                </Typography>
              </Grid>
            </Grid>
          </CardContent>
        </Card>

        {/* User ID */}
        <Card sx={{ mb: 3 }}>
          <CardContent>
            <Typography variant="h6" gutterBottom>
              User Information
            </Typography>
            <TextField
              fullWidth
              label="User ID"
              type="number"
              value={userId}
              onChange={(e) => setUserId(Number(e.target.value))}
              helperText="Enter your user ID (e.g., 1001, 1002)"
            />
          </CardContent>
        </Card>

        {/* Submit Button */}
        <Box sx={{ display: 'flex', justifyContent: 'center', mb: 4 }}>
          <Button
            variant="contained"
            size="large"
            onClick={handleSubmit}
            disabled={tripDays.length === 0}
          >
            Plan My Trip
          </Button>
        </Box>
      </Container>
    </LocalizationProvider>
  );
};

export default TripPlanner;
