# PAZ Admin Portal - Frontend

A modern, responsive Next.js 14 frontend application for the Prison Alliance Zimbabwe (PAZ) Admin Portal. This application provides a comprehensive interface for managing teachers, prisons, and educational programs within the prison system.

## Features

### 🔐 Authentication & Security
- JWT-based authentication system
- Protected routes with role-based access control
- Secure token management with automatic refresh
- Mock authentication for development and testing

### 👥 Teacher Management
- Complete CRUD operations for teachers
- Search and filter functionality
- Responsive design with mobile-friendly interface
- Form validation with React Hook Form
- Teacher specialization and experience tracking

### 🏢 Prison Management
- Comprehensive prison facility management
- Capacity monitoring with visual indicators
- Contact information management
- Mobile-responsive card-based interface
- Real-time capacity percentage calculations

### 📱 Responsive Design
- Mobile-first approach with Tailwind CSS
- Collapsible sidebar for mobile devices
- Responsive tables that convert to cards on mobile
- Touch-friendly interface elements
- Optimized for various screen sizes

### 🎨 Modern UI/UX
- Clean, professional design with Tailwind CSS
- Lucide React icons for consistent iconography
- Loading states and error handling
- Intuitive navigation and user flows

## Tech Stack

- **Framework**: Next.js 14 with App Router
- **Styling**: Tailwind CSS
- **Icons**: Lucide React
- **Forms**: React Hook Form
- **HTTP Client**: Axios
- **Authentication**: JWT with mock API
- **State Management**: React Context API

## Project Structure

```
frontend/
├── app/                    # Next.js App Router pages
│   ├── api/               # API routes
│   │   └── auth/          # Authentication endpoints
│   ├── dashboard/         # Main dashboard
│   ├── login/             # Login page
│   ├── teachers/          # Teacher management
│   └── prisons/           # Prison management
├── components/            # Reusable React components
│   ├── auth/              # Authentication components
│   ├── layout/            # Layout components
│   ├── teachers/          # Teacher-specific components
│   ├── prisons/           # Prison-specific components
│   └── common/            # Shared components
├── lib/                   # Utility libraries
│   ├── api.js            # API configuration
│   └── auth.js           # Authentication logic
└── public/               # Static assets
```

## Getting Started

### Prerequisites

- Node.js 18+ 
- npm or yarn

### Installation

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm run dev
   ```

4. Open [http://localhost:3000](http://localhost:3000) in your browser

### Demo Credentials
- **Username**: admin
- **Password**: password123
- **Email**: admin@paz.org

- **Demo User**: demo
- **Password**: password123
- **Email**: demo@paz.org.zw


## Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run start` - Start production server
- `npm run lint` - Run ESLint

## API Integration

The frontend is designed to work with the PAZ Admin backend API. Key endpoints include:

- **Authentication**: `/api/auth/login`
- **Teachers**: `/api/teachers` (GET, POST, PUT, DELETE)
- **Prisons**: `/api/prisons` (GET, POST, PUT, DELETE)

### Environment Variables

Create a `.env.local` file for environment-specific configuration:

```env
API_URL=http://localhost:8080/api
```

## Responsive Breakpoints

- **Mobile**: < 768px (sm)
- **Tablet**: 768px - 1024px (md)
- **Desktop**: > 1024px (lg)

## Performance Features

- Code splitting with Next.js App Router
- Optimized images and assets
- Efficient state management
- Minimal bundle size with tree shaking

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is part of the Prison Alliance Zimbabwe administration system.

## Support

For support and questions, please contact the development team or refer to the backend documentation.